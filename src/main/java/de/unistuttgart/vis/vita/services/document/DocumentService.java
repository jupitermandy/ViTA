package de.unistuttgart.vis.vita.services.document;

import de.unistuttgart.vis.vita.analysis.AnalysisController;
import de.unistuttgart.vis.vita.model.dao.DocumentDao;
import de.unistuttgart.vis.vita.model.document.AnalysisParameters;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.services.BaseService;
import de.unistuttgart.vis.vita.services.WordCloudService;
import de.unistuttgart.vis.vita.services.analysis.AnalysisService;
import de.unistuttgart.vis.vita.services.analysis.ProgressService;
import de.unistuttgart.vis.vita.services.entity.EntitiesService;
import de.unistuttgart.vis.vita.services.entity.PersonsService;
import de.unistuttgart.vis.vita.services.entity.PlacesService;
import de.unistuttgart.vis.vita.services.entity.PlotViewService;
import de.unistuttgart.vis.vita.services.requests.DocumentRenameRequest;
import de.unistuttgart.vis.vita.services.responses.DocumentIdResponse;
import de.unistuttgart.vis.vita.services.search.SearchInDocumentService;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.List;
import java.util.Set;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.RollbackException;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Provides methods for GET, PUT and DELETE a document with a specific id.
 */
@ManagedBean
public class DocumentService extends BaseService {

  private static final Logger LOGGER = Logger.getLogger(DocumentService.class);
  private String id;

  private DocumentDao documentDao;

  @Inject
  private AnalysisController analysisController;

  @Inject
  ProgressService progressService;

  @Inject
  ChapterService chapterService;

  @Inject
  EntitiesService entitiesService;

  @Inject
  PersonsService personsService;

  @Inject
  PlacesService placesService;

  @Inject
  DocumentPartsService partsService;

  @Inject
  AnalysisService analysisService;

  @Inject
  SearchInDocumentService searchInDocumentService;

  @Inject
  WordCloudService wordCloudService;

  @Inject
  PlotViewService plotViewService;

  @Override
  public void postConstruct() {
    super.postConstruct();
    documentDao = getDaoFactory().getDocumentDao();
  }

  /**
   * Sets the id of the document this resource should represent
   *
   * @param id the id
   */
  public DocumentService setId(String id) {
    this.id = id;
    return this;
  }

  /**
   * Reads the requested document from the database and returns it in JSON using the REST.
   *
   * @return the document with the current id in JSON
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Document getDocument() {
    Document readDoc;
    try {
      readDoc = documentDao.findById(id);
    } catch (NoResultException e) {
      throw new WebApplicationException(e, Response.status(Response.Status.NOT_FOUND).build());
    }

    return readDoc;
  }

  /**
   * Renames the current document to the given name.
   *
   * @param renameRequest - the renaming request including id and new name.
   * @return a response with no content if renaming was successful, HTTP 404 if document was not
   *         found or 500 if an error occurred.
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  public Response putDocument(DocumentRenameRequest renameRequest) {
    Response response = null;
    try {
      Document affectedDocument = documentDao.findById(id);
      affectedDocument.getMetadata().setTitle(renameRequest.getName());
      documentDao.update(affectedDocument);

      response = Response.noContent().build();
    } catch (EntityNotFoundException enfe) {
      throw new WebApplicationException(enfe, Response.status(Response.Status.NOT_FOUND).build());
    } catch (RollbackException re) {
      throw new WebApplicationException(re);
    }
    return response;
  }

  /**
   * Deletes the document with the current id.
   *
   * @return a response with no content if removal was successful, status 404 if document was not
   *         found
   */
  @DELETE
  public Response deleteDocument() {
    Response response;

    try {
      // first cancel a running analysis
      analysisController.cancelAnalysis(id);

      Document byId = documentDao.findById(id);

      // then remove it from the database
      documentDao.remove(byId);
      List<Document> sameTitle = documentDao.findDocumentsByFilename(byId.getFileName());

      if (sameTitle.size() == 0 && byId.getFilePath() != null) {
        // Can remove the file from HDD.
        File file = new File(byId.getFilePath().toUri());
        boolean delete = file.delete();

        if (!delete) {
          LOGGER.info("Could not delete file: " + byId.getFilePath());
        }
      }

      // create the response
      response = Response.noContent().build();
    } catch (NoResultException nre) {
      throw new WebApplicationException(nre, Response.status(Response.Status.NOT_FOUND).build());
    }

    // send response
    return response;
  }

  /**
   * Returns the ProgressService for the current document.
   *
   * @return progress service which answers this request
   */
  @Path("/progress")
  public ProgressService getProgress() {
    return progressService.setDocumentId(id);
  }

  /**
   * Returns the ChapterService for the current document and given chapter id.
   *
   * @param chapterId - the chapter id given in the path
   * @return the chapter service which answers this request
   */
  @Path("/chapters/{chapterId}")
  public ChapterService getChapters(@PathParam("chapterId") String chapterId) {
    return chapterService.setId(chapterId).setDocumentId(id);
  }

  /**
   * Returns the PersonsService for the current document.
   *
   * @return the PersonsService which answers this request
   */
  @Path("/persons")
  public PersonsService getPersons() {
    return personsService.setDocumentId(id);
  }

  /**
   * Returns the PlacesService for the current document.
   *
   * @return the PlacesService which answers this request
   */
  @Path("/places")
  public PlacesService getPlaces() {
    return placesService.setDocumentId(id);
  }

  /**
   * Returns the EntitiesService for the current document.
   *
   * @return the Entities which answers this request
   */
  @Path("/entities")
  public EntitiesService getEntity() {
    return entitiesService.setDocumentId(id);
  }

  /**
   * Returns the PartsService for the current document.
   *
   * @return the PartsService which answers this request
   */
  @Path("/parts")
  public DocumentPartsService getParts() {
    return partsService.setDocumentId(id);
  }

  /**
   * Return the AnalysisService for the current document.
   *
   * @return the AnalysisService which answers this request
   */
  @Path("/analysis")
  public AnalysisService stopAnalysis() {
    return analysisService.setDocumentId(id);
  }

  /**
   * Return the SearchInDocumentService for the current document.
   *
   * @return the SearchInDocumentService which answers this request
   */
  @Path("/search")
  public SearchInDocumentService getSearch() {
    return searchInDocumentService.setDocumentId(id);
  }

  /**
   * Return the WordCloudService for the current document.
   *
   * @return the WordCloudService which answers this request
   */
  @Path("/wordcloud")
  public WordCloudService getWordcloud() {
    return wordCloudService.setDocumentId(id);
  }

  /**
   * Return the PlotViewService for the current document.
   *
   * @return the PlotViewService which answers this request
   */
  @Path("/plotview")
  public PlotViewService getPlotView() {
    return plotViewService.setDocumentId(id);
  }

  @GET
  @Path("/parameters")
  @Produces(MediaType.APPLICATION_JSON)
  public AnalysisParameters getParameters() {
    Document readDoc;
    try {
      readDoc = documentDao.findById(id);
    } catch (NoResultException e) {
      throw new WebApplicationException(e, Response.status(Response.Status.NOT_FOUND).build());
    }

    return readDoc.getParameters();
  }

  /**
   * Restarts the analysis of a document with different parameters
   */
  @Path("/derive")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public DocumentIdResponse derive(AnalysisParameters parameters) {
    Set<ConstraintViolation<AnalysisParameters>> violations =
        Validation.buildDefaultValidatorFactory().getValidator().validate(parameters);
    if (!violations.isEmpty()) {
      // TODO this does not work, it produces a generic Bad Request
      throw new ValidationViolationException(violations);
    }

    Document readDoc;
    try {
      readDoc = documentDao.findById(id);
    } catch (NoResultException e) {
      throw new WebApplicationException(e, Response.status(Response.Status.NOT_FOUND).build());
    }

    Document derived = Document.copy(readDoc);
    derived.setParameters(parameters);

    // schedule analysis
    String id = analysisController.reScheduleDocumentAnalysis(derived);

    // set up Response
    return new DocumentIdResponse(id);
  }
}
