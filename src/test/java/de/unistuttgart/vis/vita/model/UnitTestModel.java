package de.unistuttgart.vis.vita.model;

import org.apache.commons.io.FileUtils;

import java.io.IOException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * A model that uses a h2 database that can be dropped at any time with {@link #startNewSession()}
 */
public class UnitTestModel extends Model {
  private static final String UNITTEST_PERSISTENCE_UNIT_DROP_NAME =
      "de.unistuttgart.vis.vita.unittest.drop";

  private static EntityManagerFactory emfCache;

  public UnitTestModel() {
    super(getEntityManagerFactory(), new TextRepository(new UnitTestDirectoryFactory()));
    setGateDatastoreLocation(new UnitTestGateDatastoreLocation());
  }

  private static EntityManagerFactory getEntityManagerFactory() {
    if (emfCache == null)
      emfCache = Persistence.createEntityManagerFactory(UNITTEST_PERSISTENCE_UNIT_DROP_NAME);
    return emfCache;
  }

  /**
   * Drops the old database so that the next instance of {@link UnitTestModel} will work on a fresh
   * database
   */
  public static void startNewSession() {
    if (emfCache != null) {
      if (emfCache.isOpen())
        emfCache.close();
      emfCache = null;
    }
    try {
      FileUtils.deleteDirectory(UnitTestDirectoryFactory.getRootPath().toFile());
      FileUtils.deleteDirectory(UnitTestGateDatastoreLocation.getRootPath().toFile());
    } catch (IOException e) {
      throw new RuntimeException("Unable to delete folder:", e);
    }
  }
}
