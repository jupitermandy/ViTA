package de.unistuttgart.vis.vita.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.TypedQuery;

import org.junit.Test;

import de.unistuttgart.vis.vita.model.entity.Place;

public class PlacePersistenceTest extends AbstractPersistenceTest {
  
  // test data
  private static final int TEST_PLACE_RANKING_VALUE = 3;
  private static final String TEST_PLACE_NAME = "Rivendell";

  /**
   * Checks whether one Place can be persisted correctly.
   */
  @Test
  public void testPersistOnePlace() {
    // first set up a place
    Place testPlace = createTestPlace();

    // persist this place
    em.persist(testPlace);
    startNewTransaction();

    // read places from the database
    List<Place> places = readPlacesFromDb();
    assertEquals(1, places.size());
    Place readPlace = places.get(0);
    
    checkData(readPlace);
  }

  /**
   * Creates a new Place, sets attributes to test values and returns it.
   * 
   * @return test place
   */
  private Place createTestPlace() {
    Place testPlace = new Place();
    testPlace.setDisplayName(TEST_PLACE_NAME);
    testPlace.setRankingValue(TEST_PLACE_RANKING_VALUE);
    return testPlace;
  }

  /**
   * Reads Places from database and returns them.
   * 
   * @return list of places
   */
  private List<Place> readPlacesFromDb() {
    TypedQuery<Place> query = em.createQuery("from Place", Place.class);
    return query.getResultList();
  }
  
  /**
   * Checks whether the given place is not <code>null</code> and includes the correct test data.
   * 
   * @param readPlace - the place which should be checked
   */
  private void checkData(Place readPlace) {
    assertNotNull(readPlace);
    assertEquals(TEST_PLACE_NAME, readPlace.getDisplayName());
    assertEquals(TEST_PLACE_RANKING_VALUE, readPlace.getRankingValue());
  }
  
  /**
   * Checks whether all Named Queries of Place are working correctly.
   */
  @Test
  public void testNamedQueries() {
    Place testPlace = createTestPlace();
    
    em.persist(testPlace);
    startNewTransaction();
    
    // check Named Query finding all places
    TypedQuery<Place> allQ = em.createNamedQuery("Place.findAllPlaces", Place.class);
    List<Place> allPlaces = allQ.getResultList();
    
    assertTrue(allPlaces.size() > 0);
    Place readPlace = allPlaces.get(0);
    checkData(readPlace);
    
    int id = readPlace.getId();
    
    // check Named Query finding place by id
    TypedQuery<Place> idQ = em.createNamedQuery("Place.findPlaceById", Place.class);
    idQ.setParameter("placeId", id);
    Place idPlace = idQ.getSingleResult();
    
    checkData(idPlace);
    
    // check Named Query finding places by name
    TypedQuery<Place> nameQ = em.createNamedQuery("Place.findPlaceByName", Place.class);
    nameQ.setParameter("placeName", TEST_PLACE_NAME);
    List<Place> namePlaces = nameQ.getResultList();
    
    assertTrue(namePlaces.size() > 0);
    Place namePlace = namePlaces.get(0);
    checkData(namePlace);
  }

}
