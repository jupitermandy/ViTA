package de.unistuttgart.vis.vita.services;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.entity.Entity;
import de.unistuttgart.vis.vita.model.entity.EntityRelation;
import de.unistuttgart.vis.vita.services.responses.RelationConfiguration;
import de.unistuttgart.vis.vita.services.responses.RelationsResponse;

/**
 * Provides method returning EntityRelations requested using GET.
 */
public class EntityRelationsService {
  
  private EntityManager em;
  
  @Inject
  public EntityRelationsService(Model model) {
    em = model.getEntityManager();
  }
  
  /**
   * Returns the EntityRelations matching the given criteria.
   * 
   * @param steps - maximum amount of elements in the result collection
   * @param rangeStart - the relative start position of the text to be checked
   * @param rangeEnd - the relative end position of the text to be checked
   * @param eIds - the ids of the entities for which relations should be found, as a 
   *                    comma-separated String
   * @param type - the type of the entities
   * @return the response including the relations
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public RelationsResponse getRelations(@QueryParam("steps") int steps,
                                        @QueryParam("rangeStart") double rangeStart,
                                        @QueryParam("rangeEnd") double rangeEnd,
                                        @QueryParam("entityIds") String eIds,
                                        @QueryParam("type") String type) {
    // first check all parameters
    if (steps <= 0) {
      throw new WebApplicationException("Illegal amount of steps!");
    } else if (!isValidRangeValue(rangeStart) || !isValidRangeValue(rangeEnd)) {
      throw new WebApplicationException("Illegal range!");
    } else if (eIds == null || "".equals(eIds)) {
      throw new WebApplicationException("No entities specified!");
    } else {
      switch (type) {
        case "person":
          break;
        case "place":
          break;
        case "all":
          break;
        default:
          throw new WebApplicationException("Unknown type, must be 'person', 'place' or 'all'!");
      }
    }
    
    // convert entity id string
    List<String> entityIds = new ArrayList<>();
    for (String substring : eIds.replaceAll("\\[|\\]","").split(",")) {
      entityIds.add(substring.trim());
    }
    
    // get relations from database
    List<EntityRelation<Entity>> relations = readRelationsFromDatabase(steps, entityIds); 
    
    // create the response and return it
    return new RelationsResponse(entityIds, createConfiguration(relations));
  }

  /**
   * Reads the wanted EntityRelations from the database.
   * 
   * @param steps - the maximum amount of EntityRelations being returned
   * @param ids - the list of entity id to be searched for
   * @return list of EntityRelations matching the given criteria
   */
  @SuppressWarnings("unchecked")
  private List<EntityRelation<Entity>> readRelationsFromDatabase(int steps, List<String> ids) {
    Query query = em.createNamedQuery("EntityRelation.findRelationsForEntities");
    query.setParameter("entityIds", ids);
    query.setMaxResults(steps);
    return query.getResultList();
  }

  /**
   * Creates a list of RelationConfigurations by mapping the given EntityRelations to a flat 
   * representation.
   * 
   * @param relations - the EntityRelations to be mapped
   * @return the configurations as a flat representation of the given relations
   */
  private List<RelationConfiguration> createConfiguration(List<EntityRelation<Entity>> relations) {
    List<RelationConfiguration> configurations = new ArrayList<>();
    for (EntityRelation<Entity> entityRelation : relations) {
      configurations.add(new RelationConfiguration(entityRelation));
    }
    return configurations;
  }
  
  /**
   * Returns whether given number is a valid range value or not.
   * 
   * @param value - the value to be checked
   * @return true if given value is a valid range value, false otherwise
   */
  private boolean isValidRangeValue(double value) {
    return value >= 0.0 && value <= 1.0;
  }

}
