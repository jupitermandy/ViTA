/*
 * AnnieStoreModule.java
 *
 */

package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.AnnieDatastore;
import de.unistuttgart.vis.vita.model.Model;

import java.io.File;
import java.net.URI;
import java.util.logging.Logger;

import gate.Corpus;
import gate.DataStore;
import gate.Factory;
import gate.FeatureMap;
import gate.LanguageResource;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.persist.SerialDataStore;

/**
 *
 */
@AnalysisModule(dependencies = {GateInitializeModule.class, Model.class})
public class AnnieStoreModule extends Module<AnnieDatastore> {

  private static final Logger LOGGER = Logger.getLogger(AnnieStoreModule.class.getName());
  private static final String LR_TYPE_CORP = "gate.corpora.SerialCorpusImpl";
  private SerialDataStore serialDataStore;

  @Override
  public AnnieDatastore execute(ModuleResultProvider results, ProgressListener progressListener)
      throws Exception {
    Model model = results.getResultFor(Model.class);
    URI location = model.getGateDatastoreLocation().getLocation();

    File datastoreDIR = new File(location);

    if (!datastoreDIR.exists()) {
      serialDataStore = (SerialDataStore) Factory.createDataStore("gate.persist.SerialDataStore",
                                                                  location.toString());
    } else {
      serialDataStore = new SerialDataStore(location.toString());
    }

    return buildResult();
  }

  private AnnieDatastore buildResult() {
    return new AnnieDatastore() {
      @Override
      public Corpus getStoredAnalysis(String documentID) throws PersistenceException {
        serialDataStore.open();
        FeatureMap corpFeatures = Factory.newFeatureMap();
        corpFeatures.put(DataStore.LR_ID_FEATURE_NAME, documentID);
        corpFeatures.put(DataStore.DATASTORE_FEATURE_NAME, serialDataStore);

        try {
          Corpus resource =
              (Corpus) Factory.createResource("gate.corpora.SerialCorpusImpl", corpFeatures);
          LOGGER.info("Corpus loaded from datastore!");
          serialDataStore.close();
          return resource;
        } catch (ResourceInstantiationException e) {
          // Resource could not be created so it possibly doesn't exists.
        } finally {
          serialDataStore.close();
        }

        return null;
      }

      @Override
      public DataStore getDatastore() {
        return serialDataStore;
      }

      @Override
      public void storeResult(LanguageResource resource, String documentID)
          throws PersistenceException {
        serialDataStore.open();

        try {
          LanguageResource adopt = serialDataStore.adopt(resource);
          adopt.setLRPersistenceId(documentID);
          serialDataStore.sync(adopt);
          LOGGER.info("Corpus saved in datastore!");
        } finally {
          serialDataStore.close();
        }
      }

      @Override
      public void removeResult(String documentID) throws PersistenceException {
        serialDataStore.open();

        try {
          serialDataStore.delete(LR_TYPE_CORP, documentID);
          LOGGER.info("Corpus with ID: " + documentID + " removed!");
        } finally {
          serialDataStore.close();
        }
      }
    };
  }
}
