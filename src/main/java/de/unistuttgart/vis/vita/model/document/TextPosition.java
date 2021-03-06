package de.unistuttgart.vis.vita.model.document;

import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.persistence.Embeddable;

/**
 * Represents the position of a single character in the text of a Document. It is aware of the
 * chapter it occurs in as well as the relative position in the whole document.
 */
@Embeddable
public class TextPosition implements Comparable<TextPosition> {

  private int offset;
  private double progress;

  /**
   * Creates a new instance of TextPosition.
   * <p>
   * Use factory methods {@link TextPosition#fromGlobalOffset(int, int)} and
   * {@link TextPosition#fromLocalOffset(Chapter, int, int)} instead to avoid misunderstandings
   * concerning the offsets.
   * </p>
   */
  protected TextPosition() {
    // no-argument constructor needed for JPA
  }

  /**
   * Creates a new TextPosition by specifying the chapter and the document-wide character offset
   *
   * @param pOffset - the global offset of this TextPosition within the document
   * @param documentLength - the length of the whole document
   */
  private TextPosition(int pOffset, int documentLength) {
    /* This constructor is private to prevent confusion about global/local offsets
    The factory methods should be used instead. */
    if (pOffset < 0) {
      throw new IllegalArgumentException("offset must not be negative!");
    }
    if (documentLength < 0) {
      throw new IllegalArgumentException("documentLength must not be negative");
    }

    this.offset = pOffset;
    if (documentLength == 0) {
      this.progress = 0.0f;
    } else {
      this.progress = (1.0 * pOffset) / (1.0 * documentLength);
    }
  }

  /**
   * Creates a new TextPosition by specifying the chapter and the chapter-local character offset
   *
   * @param chapter - the chapter this TextPosition lies in
   * @param localOffset - the offset of this TextPosition within the chapter
   * @param documentLength - the length of the whole document
   */
  public static TextPosition fromLocalOffset(Chapter chapter, int localOffset, int documentLength) {
    if (chapter == null)
      throw new NullPointerException("chapter is null");
    return new TextPosition(chapter.getRange().getStart().getOffset() + localOffset, documentLength);
  }

  /**
   * Creates a new TextPosition by specifying the chapter and the document-wide character offset
   *
   * @param globalOffset - the global offset of this TextPosition within the document
   * @param documentLength - the length of the whole document
   */
  public static TextPosition fromGlobalOffset(int globalOffset, int documentLength) {
    if (globalOffset > documentLength) {
      throw new IllegalArgumentException(
          "The global offset can not be higher than the document length!");
    }
    return new TextPosition(globalOffset, documentLength);
  }

  /**
   * @return the global offset of this TextPosition within the document
   */
  public int getOffset() {
    return offset;
  }

  /**
   * Sets the offset of this TextPosition to the given value.
   *
   * @param newOffset - the new character offset of this TextPosition
   */
  public void setOffset(int newOffset) {
    this.offset = newOffset;
  }

  /**
   * Gets the offset relative to the enclosing chapter. This method will only produce a useful
   * result if the given chapter is the enclosing chapter.
   *
   * @param enclosingChapter The enclosing chapter
   * @return the local offset
   */
  public int getLocalOffset(Chapter enclosingChapter) {
    if(enclosingChapter == null){
      throw new IllegalArgumentException("Chapter must not be null");
    }
    
    return offset - enclosingChapter.getRange().getStart().getOffset();
  }

  /**
   * @return the progress of the TextPosition in the document. Should be a number between 0.0 and
   * 1.0.
   */
  public double getProgress() {
    return progress;
  }

  /**
   * Sets the progress for this TextPosition.
   *
   * @param progress - the relative position within the document as a number between 0.0 and 1.0
   */
  public void setProgress(double progress) {
    this.progress = progress;
  }

  /**
   * Compares this position to the given other one. This will only produce usable results if both
   * TextPositions are in the same document.
   */
  @Override
  public int compareTo(TextPosition o) {
    if (o == null) {
      return 1;
    }

    return Integer.compare(offset, o.offset);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof TextPosition)) {
      return false;
    }

    TextPosition other = (TextPosition) obj;
    /* do not compare chapters, because the position between to chapters may be attributed to two
    different chapters, and they are still the same TextPosition */
    return other.offset == this.offset;
  }

  @Override
  public int hashCode() {
    /* do not compare chapters, because the position between to chapters may be attributed to two
    different chapters, and they are still the same TextPosition */
    return new HashCodeBuilder().append(offset).hashCode();
  }

  @Override
  public String toString() {
    return String.format("[%d]", offset);
  }

  /**
   * Returns the text position further to the end of the document
   * 
   * @param a - first TextPosition to compare
   * @param b - second TextPosition to compare
   * @return either a or b
   */
  public static TextPosition max(TextPosition a, TextPosition b) {
    if (a.compareTo(b) > 0) {
      return a;
    }
    return b;
  }

  /**
   * Returns the text position further at the beginning of the document
   * 
   * @param a - the first TextPosition to compare
   * @param b - the second TextPosition to compare
   * @return either a or b
   */
  public static TextPosition min(TextPosition a, TextPosition b) {
    if (a.compareTo(b) < 0) {
      return a;
    }
    return b;
  }

}
