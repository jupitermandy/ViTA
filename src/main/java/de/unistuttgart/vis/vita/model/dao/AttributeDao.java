package de.unistuttgart.vis.vita.model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.MappedSuperclass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.model.entity.Attribute;

/**
 * Represents a data access object for accessing Attributes.
 */
@MappedSuperclass
@NamedQueries({
  @NamedQuery(name = "Attribute.findAllAttributes",
              query = "SELECT a "
                      + "FROM Attribute a"),

  @NamedQuery(name = "Attribute.findAttributeById",
              query = "SELECT a "
                      + "FROM Attribute a "
                      + "WHERE a.id = :attributeId"),

  @NamedQuery(name = "Attribute.findAttributesForEntity",
              query = "SELECT a "
                      + "FROM Attribute a, Entity e "
                      + "WHERE e.id = :entityId "
                      + "AND a MEMBER OF e.attributes"),

  @NamedQuery(name = "Attribute.findAttributeByType",
              query = "SELECT a "
                      + "FROM Attribute a "
                      + "WHERE a.type = :attributeType")}
)
public class AttributeDao extends JpaDao<Attribute, String> {
  
  private static final String ATTRIBUTE_TYPE_PARAMETER = "attributeType";
  private static final String ENTITY_ID_PARAMETER = "entityId";

  /**
   * Creates a new data access object for accessing Attributes using the given {@link EntityManager}.
   * 
   * @param em - the EntityManager to be used in the new AttributeDao
   */
  public AttributeDao(EntityManager em) {
    super(Attribute.class, em);
  }

  /**
   * Finds all Attributes for an entity with a given id.
   * 
   * @param entityId - the id of the entity
   * @param offset - the first Attribute to be returned
   * @param count - the maximum amount of Attributes to be returned
   * @return a list of attributes of the given entity
   */
  public List<Attribute> findAttributesForEntity(String entityId, int offset, int count) {
    TypedQuery<Attribute> entityQuery = em.createNamedQuery("Attribute.findAttributesForEntity",
                                                            Attribute.class);
    entityQuery.setParameter(ENTITY_ID_PARAMETER, entityId);
    entityQuery.setFirstResult(offset);
    entityQuery.setMaxResults(count);
    return entityQuery.getResultList();
  }
  
  /**
   * Finds all Attributes of a given type.
   * 
   * @param attributeType - the type of attribute
   * @param offset - the first Attribute to be returned
   * @param count - the maximum amount of Attributes to be returned
   * @return a list of all Attributes with the given type
   */
  public List<Attribute> findAttributeForType(String attributeType, int offset, int count) {
    TypedQuery<Attribute> typeQuery = em.createNamedQuery("Attribute.findAttributeByType",
                                                          Attribute.class);
    typeQuery.setParameter(ATTRIBUTE_TYPE_PARAMETER, attributeType);
    typeQuery.setFirstResult(offset);
    typeQuery.setMaxResults(count);
    return typeQuery.getResultList();
  }

}
