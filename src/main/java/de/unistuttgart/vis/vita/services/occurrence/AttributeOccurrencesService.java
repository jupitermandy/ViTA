package de.unistuttgart.vis.vita.services.occurrence;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.model.document.Occurrence;
import de.unistuttgart.vis.vita.model.document.Range;
import de.unistuttgart.vis.vita.services.responses.occurrence.OccurrencesResponse;

/**
 * Provides a method to GET the occurrences of the current attribute and entity.
 */
@ManagedBean
public class AttributeOccurrencesService extends ExtendedOccurrencesService {

  private String attributeId;

  private String entityId;

  /**
   * Sets the id of the document this service refers to and returns this
   * AttributeOccurrencesService.
   * 
   * @param docId - the id of the document in which this service should find occurrences of
   *        attributes
   * @return this AttributeOccurrencesService
   */
  public AttributeOccurrencesService setDocumentId(String docId) {
    this.documentId = docId;
    return this;
  }

  /**
   * Sets the id of the entity which occurrences should be got and returns this
   * AttributeOccurrencesService.
   * 
   * @param eId - the id of the entity which occurrences should be got
   * @return this AttributeOccurrencesService
   */
  public AttributeOccurrencesService setEntityId(String eId) {
    this.entityId = eId;
    return this;
  }

  /**
   * Sets the id of the attribute which occurrences should be got and returns this
   * AttributeOccurrencesService.
   * 
   * @param attrId - the id of the attribute which occurrences should be got
   * @return this AttributeOccurrencesService
   */
  public AttributeOccurrencesService setAttributeId(String attrId) {
    this.attributeId = attrId;
    return this;
  }

  /**
   * Reads occurrences of the specific attribute and entity from database and returns them in JSON.
   * 
   * @param steps - amount of steps, the range should be divided into (default value 0 means exact)
   * @param rangeStart - start of range to be searched in
   * @param rangeEnd - end of range to be searched in
   * @return an OccurenceResponse holding all found Occurrences
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public OccurrencesResponse getOccurrences(@DefaultValue("0") @QueryParam("steps") int steps,
      @QueryParam("rangeStart") double rangeStart,
      @QueryParam("rangeEnd") @DefaultValue("1") double rangeEnd) {
    return getOccurrencesImpl(steps, rangeStart, rangeEnd);
  }
  
  @Override
  protected List<Range> getExactEntityOccurrences(int startOffset, int endOffset) {
    // get the Occurrences
    List<Occurrence> readOccurrences =
        occurrenceDao.findOccurrencesForAttribute(entityId, attributeId, startOffset, endOffset);

    // convert Occurrences into Ranges and return them
    return Range.mergeOverlappingRanges(convertOccurrencesToRanges(readOccurrences));
  }

  @Override
  protected boolean hasOccurrencesInStep(int firstSentenceIndex, int lastSentenceIndex) {
    return occurrenceDao.getNumberOfOccurrencesForAttribute(entityId, attributeId,
        firstSentenceIndex, lastSentenceIndex) > 0;
  }

}
