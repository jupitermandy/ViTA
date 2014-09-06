package de.unistuttgart.vis.vita.services;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.data.ProgressTestData;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.progress.AnalysisProgress;

/**
 * Performs a test on the ProgressService, requesting the progress for a test document using GET.
 */
public class ProgressServiceTest extends ServiceTest {
  
  private String documentId;
  private ProgressTestData testData;
  
  /**
   * Persist test document for which the progress is requested.
   */
  @Override
  public void setUp() throws Exception {
    super.setUp();
    
    // persist test document
    EntityManager em = Model.createUnitTestModel().getEntityManager();
    
    DocumentTestData documenttestData = new DocumentTestData();
    
    Document testDoc1 = documenttestData.createTestDocument(1);
    
    documentId = testDoc1.getId();
    
    em.getTransaction().begin();
    em.persist(testDoc1);
    em.getTransaction().commit();
    em.close();
    
    // create test data instance
    testData = new ProgressTestData();
  }
  
  @Override
  protected Application configure() {
    return new ResourceConfig(ProgressService.class);
  }

  /**
   * Tests whether progress is returned by progress service.
   */
  @Test
  public void testGetProgress() {
    String path = "documents/" + documentId + "/progress/";
    AnalysisProgress actualProgress = target(path).request().get(AnalysisProgress.class);

    // check data
    testData.checkData(actualProgress);
  }

}
