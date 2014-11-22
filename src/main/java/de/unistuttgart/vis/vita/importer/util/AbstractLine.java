package de.unistuttgart.vis.vita.importer.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Basic Line which implements constructors, getters and setters and 
 */
public abstract class AbstractLine implements Line {
  // Patterns for Types - static so only one has to be compiled for all existing Lines.

  private static final String WHITESPACE = "([^\\S\\p{Graph}])*";
  protected static final Pattern WHITESPACEPATTERN = Pattern.compile(WHITESPACE);

  private static final String DATADIVIDER = WHITESPACE + "(\\*\\*\\*)(.*)(\\*\\*\\*)" + WHITESPACE;
  protected static final Pattern DATADIVIDERPATTERN = Pattern.compile(DATADIVIDER);

  private static final String MARKEDHEADING = WHITESPACE + "#.*";
  protected static final Pattern MARKEDHEADINGPATTERN = Pattern.compile(MARKEDHEADING);

  private static final String PREFACE = WHITESPACE + "((Preface)|(To\\s*the\\s*Reader))([\\.:])?"
      + WHITESPACE;
  protected static final Pattern PREFACEPATTERN = Pattern.compile(PREFACE, Pattern.CASE_INSENSITIVE);

  private static final String TABLEOFCONTENTS = WHITESPACE
      + "((Index)|(Contents)|(Table\\s*of\\s*Contents))([\\.:])?" + WHITESPACE;
  protected static final Pattern TABLEOFCONTENTSPATTERN = Pattern.compile(TABLEOFCONTENTS,
      Pattern.CASE_INSENSITIVE);

  private static final String BIGHEADING = WHITESPACE + "([\\p{Upper}\\d][^\\p{Lower}]*)";
  private static final String BIGHEADINGQUOTES = WHITESPACE + "((\"" + BIGHEADING + "\")" + "|"
      + "(\'" + BIGHEADING + "\'))" + WHITESPACE;
  private static final String EXTENDEDBIGHEADING = WHITESPACE + "_" + "(" + BIGHEADING + "|"
      + BIGHEADINGQUOTES + ")" + "_" + WHITESPACE;
  protected static final Pattern BIGHEADINGPATTERN = Pattern.compile("(" + BIGHEADING + ")|("
      + BIGHEADINGQUOTES + ")|(" + EXTENDEDBIGHEADING + ")");

  private static final String NORMALHEADING = "(([\\d]+\\.)|([IVXML]+\\.))?" + WHITESPACE
      + "\\p{Upper}[^\\.\\?\\!]*\\.?";
  private static final String CHAPTER = "(?i)Chapter(?-i)" + WHITESPACE + "((\\d+)|([IVXML]+))?"
      + WHITESPACE + "\\p{Punct}?";
  private static final String ONEQUOTE = "((\").*(\"))|((\').*(\'))";
  private static final String SMALLHEADING = "(" + WHITESPACE + "(" + CHAPTER + ")?" + WHITESPACE
      + "((" + NORMALHEADING + ")" + "|" + "(" + ONEQUOTE + "))" + WHITESPACE + ")|(" + CHAPTER
      + ")";
  private static final String EXTENDEDSMALLHEADING = WHITESPACE + "_" + SMALLHEADING + "_"
      + WHITESPACE;
  protected static final Pattern SMALLHEADINGPATTERN = Pattern.compile(SMALLHEADING + "|"
      + EXTENDEDSMALLHEADING);

  private static final String NOSPECIALSIGNS = "\\p{Alnum}";
  protected static final Pattern NOSPECIALSIGNSPATTERN = Pattern.compile(NOSPECIALSIGNS);
  
  protected String text;
  protected LineType type;
  protected boolean automatedTypeComputation;

  /**
   * Creates a simple Line with activated type computation.
   *
   * @param text String - The text of the line. Should not be null.
   */
  public AbstractLine(String text) {
    this(text, true);
  }

  /**
   * Creates a simple Line. If the type computation is deactivated the type is set to UNKNOWN. You
   * can compute the type manually by calling 'computeType()'.
   *
   * @param text                     String - The text of the line. Should not be null.
   * @param automatedTypeComputation Boolean - True activates the automated type computation, which
   *                                 means every change to the text will update the type.
   */
  public AbstractLine(String text, Boolean automatedTypeComputation) {
    super();
    this.type = LineType.UNKNOWN;
    this.text = text;
    this.automatedTypeComputation = automatedTypeComputation;
    computeType();
  }

  @Override
  public String getText() {
    return text;
  }

  @Override
  public void setText(String text) {
    this.text = text;
    computeType();
  }

  @Override
  public LineType getType() {
    return type;
  }

  @Override
  public void setType(LineType type) {
    this.type = type;
  }

  @Override
  public boolean isAutomatedTypeComputation() {
    return automatedTypeComputation;
  }

  @Override
  public void setAutomatedTypeComputation(boolean automatedTypeComputation) {
    this.automatedTypeComputation = automatedTypeComputation;
    computeType();
  }

  @Override
  public abstract void computeType();

  /**
   * Checks if pattern matches the text.
   *
   * @param pattern Pattern - The pattern.
   * @return Boolean - true is a match, false is not.
   */
  protected boolean matchesPattern(Pattern pattern) {
    Matcher matcher = pattern.matcher(text);
    return matcher.matches();
  }

  /**
   * Checks if pattern can be found in the text.
   *
   * @param pattern Pattern - The pattern.
   * @return Boolean - true if there is at least one occurrence.
   */
  protected boolean containsPattern(Pattern pattern) {
    return pattern.matcher(text).find();
  }
  
}