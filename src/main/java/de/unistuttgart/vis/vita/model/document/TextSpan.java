package de.unistuttgart.vis.vita.model.document;

/**
 * Represents a text block with a specific start and end.
 */
public class TextSpan {

  // constants
  private static final int MIN_LENGTH = 0;

  // attributes
  private final TextPosition start;
  private final TextPosition end;
  private final int length;

  /**
   * Creates a new instance of TextSpan with the given start and end position in the text.
   *
   * @param pStart - the TextPosition where the new TextSpan should begin
   * @param pEnd   - the TextPosiotion where the new TextSpan should end
   */
  public TextSpan(TextPosition pStart, TextPosition pEnd) {
    if (pStart == null || pEnd == null) {
      throw new IllegalArgumentException("positions must not be null!");
    }

    int diff = pEnd.getOffset() - pStart.getOffset();
    if (diff < MIN_LENGTH) {
      throw new IllegalArgumentException("end position must not be before start position!");
    }

    this.start = pStart;
    this.end = pEnd;
    this.length = diff;
  }

  /**
   * @return the start of the TextSpan
   */
  public TextPosition getStart() {
    return start;
  }

  /**
   * @return the end of the TextSpan
   */
  public TextPosition getEnd() {
    return end;
  }

  /**
   * @return the length of the TextSpan
   */
  public int getLength() {
    return length;
  }

}