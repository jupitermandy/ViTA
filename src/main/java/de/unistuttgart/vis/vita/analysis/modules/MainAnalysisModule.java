package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;

/**
 * The target module that depends on all enabled feature modules so that they will be executed in
 * the analysis.
 */
@AnalysisModule(dependencies = { EntityFeatureModule.class })
public class MainAnalysisModule extends Module<Void> {
  @Override
  public Void execute(ModuleResultProvider result, ProgressListener progressListener) {
    return null;
  }
}
