package de.unistuttgart.vis.vita.importer.util;


/**
 * A Line contains a text-line of the imported file. The Line class can compute a type (and a
 * subtype) for the line for further analysis of the text.
 */
public interface Line {

  /**
   * Gets the text of the Line.
   *
   * @return The text of the Line.
   */
  public String getText();

  /**
   * Sets the text for the Line. When the automated type computation is activated, the type can be
   * changed too.
   *
   * @param text The text of the Line.
   */
  public void setText(String text);

  /**
   * Checks if the line-text fits to the given type.
   * 
   * @param type The given type.
   * @return true: type fits to text; false: does not fit to the text
   */
  public boolean isType(LineType type);

  /**
   * Checks if the line-text fits to one of the given types.
   * 
   * @param types The given types.
   * @return true: at least one type fits to text; false: none of the types fits to the text
   */
  public boolean isType(Iterable<LineType> types);

  /**
   * Sets the type of the text. Should only be used if automated type computation is deactivated. To
   * set the type manually should be an exception, if it is impossible for the line to compute its
   * type itself.
   *
   * @param type The type of the text.
   */
  public void setType(LineType type);

  /**
   * Checks whether this line has a subtype or not.
   * 
   * @return true: has a subtype; false: has no subtype.
   */
  public boolean hasSubType();

  /**
   * Checks if the line-text fits to the given subtype.
   * 
   * @param subType the given subtype
   * @return true: fits to the given subtype; false: fits not to the given subtype, also if there is
   *         none.
   */
  public boolean isSubType(LineSubType subType);

  /**
   * Checks if the automated type computation is activated.
   *
   * @return true: is activated. false: is deactivated.
   */
  public boolean isAutomatedTypeComputation();

  /**
   * Activates/Deactivates the automated type computation. If activated, will compute the current
   * type instantly.
   *
   * @param automatedTypeComputation true: activate. false: deactivate.
   */
  public void setAutomatedTypeComputation(boolean automatedTypeComputation);

  /**
   * Computes the type for the text.
   */
  public void computeType();

}
