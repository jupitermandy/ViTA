package de.unistuttgart.vis.vita.persistence;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.model.dao.OccurrenceDao;
import org.junit.Test;

import de.unistuttgart.vis.vita.data.ChapterTestData;
import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.data.PersonTestData;
import de.unistuttgart.vis.vita.data.OccurrenceTestData;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Occurrence;
import de.unistuttgart.vis.vita.model.document.Range;
import de.unistuttgart.vis.vita.model.document.Sentence;
import de.unistuttgart.vis.vita.model.entity.Person;

/**
 * Performs tests whether instances of Occurrences can be persisted correctly.
 */
public class OccurrencePersistenceTest extends AbstractPersistenceTest {

  private static final int TEST_RANGE_START = 10000;
  private static final int TEST_RANGE_END = 11000;
  private static final int TEST_RANGE_DIFF = 1000;

  private static final int TEST_SENTENCE_RANGE_START = 9000;
  private static final int TEST_SENTENCE_RANGE_END = 12000;

  private Chapter chapter = new Chapter();

  @Test
  public void testPersistOneOccurrence() {
    // first set up an Occurrence
    em.persist(chapter);
    Occurrence oc = createTestOccurrence();

    // persist this Occurrence
    em.persist(oc);
    startNewTransaction();

    // read persisted Occurrence from database
    Occurrence readOccurrence = new OccurrenceDao(em).findById(oc.getId());

    // check whether data is correct
    checkData(readOccurrence);
  }

  /**
   * Creates a new Occurrence, setting start and end to test values.
   * 
   * @return test occurrence
   */
  private Occurrence createTestOccurrence() {
    Range range =
        new Range(chapter, TEST_RANGE_START, TEST_RANGE_END,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Range sentenceRange =
        new Range(chapter, TEST_SENTENCE_RANGE_START, TEST_SENTENCE_RANGE_END,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Sentence sentence = new Sentence(sentenceRange, chapter,0);
    return new Occurrence(sentence, range);
  }

  /**
   * Checks whether the given Occurrence is not <code>null</code> and includes the correct test data.
   * 
   * @param occurrenceToCheck - the Occurrence which should be checked
   */
  private void checkData(Occurrence occurrenceToCheck) {
    assertNotNull(occurrenceToCheck);
    assertNotNull(occurrenceToCheck.getId());

    int start = occurrenceToCheck.getRange().getStart().getOffset();
    int end = occurrenceToCheck.getRange().getEnd().getOffset();
    int diff = end - start;
    int sentenceStart = occurrenceToCheck.getSentence().getRange().getStart().getOffset();
    int sentenceEnd = occurrenceToCheck.getSentence().getRange().getEnd().getOffset();

    assertEquals(TEST_RANGE_START, start);
    assertEquals(TEST_RANGE_END, end);
    assertEquals(TEST_RANGE_DIFF, diff);
    assertEquals(TEST_SENTENCE_RANGE_START, sentenceStart);
    assertEquals(TEST_SENTENCE_RANGE_END, sentenceEnd);
  }

  /**
   * Checks whether all Named Queries of Occurrence are working correctly.
   * 
   * @throws Exception
   */
  @Test
  public void testFindingAllAndSpecificOccurrence() {
    Occurrence testOccurrence = createTestOccurrence();

    em.persist(chapter);
    em.persist(testOccurrence);
    startNewTransaction();

    // check Named Query finding all chapters
    OccurrenceDao occurrenceDao = new OccurrenceDao(em);
    List<Occurrence> allOccurrences = occurrenceDao.findAll();

    assertTrue(allOccurrences.size() > 0);
    Occurrence readOccurrence = allOccurrences.get(0);
    checkData(readOccurrence);

    String id = readOccurrence.getId();

    // check Named Query finding occurrences by id
    Occurrence idOccurrence = occurrenceDao.findById(id);
    checkData(idOccurrence);
  }

  /**
   * Checks whether Occurrence for a specific entity can be found using a named query.
   */
  @Test
  public void testFindingOccurrenceForEntity() {
    // first set up test data
    OccurrenceTestData testData = new OccurrenceTestData();
    Chapter c = new ChapterTestData().createTestChapter();
    Occurrence personOccurrence = testData.createOccurrence(c);
    Person testPerson = new PersonTestData().createTestPerson(1);
    testPerson.getOccurrences().add(personOccurrence);

    // save ids for query
    String chapterId = c.getId();
    String personId = testPerson.getId();

    // now persist it
    em.persist(c);
    em.persist(personOccurrence);
    em.persist(testPerson);
    startNewTransaction();

    // read Occurrences from database
    TypedQuery<Occurrence> personQ =
        em.createNamedQuery("Occurrence.findOccurrencesForEntity", Occurrence.class);
    personQ.setParameter("entityId", personId);
    personQ.setParameter("rangeStart", 0);
    personQ.setParameter("rangeEnd", 200000);

    List<Occurrence> actualPersonOccurrences = personQ.getResultList();

    // finally check read data
    assertEquals(1, actualPersonOccurrences.size());
    testData.checkData(actualPersonOccurrences.get(0), chapterId);
  }

}
