package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;

@AnalysisModule(dependencies = {Boolean.class, String.class})
public class DualDependentModule implements Module<Float> {
  @Override
  public <T> void observeProgress(Class<T> resultClass, double progress, boolean isReady) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Float execute(ModuleResultProvider result, ProgressListener progressListener) {
    // TODO Auto-generated method stub
    return null;
  }
}
