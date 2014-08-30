package de.unistuttgart.vis.vita.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

/**
 * Controls the execution of the analysis of a single document
 */
public class AnalysisExecutor {
  /**
   * Stores the modules that have not yet been started, with the modules they are waiting for
   */
  private List<ModuleExecutionState> scheduledModules;
  
  /**
   * Stores the modules that are currently executing
   */
  private List<ModuleExecutionState> runningModules;
  
  /**
   * Stores the modules that have encountered an exception and their exception
   */
  private Map<ModuleClass, Exception> failedModules = new HashMap<>();

  private AnalysisStatus status = AnalysisStatus.READY;
  /**
   * Creates an executor for the scheduled modules
   * @param scheduledModules the modules to execute
   */
  AnalysisExecutor(Iterable<ModuleExecutionState> scheduledModules) {
    this.scheduledModules = Lists.newArrayList(scheduledModules);
    runningModules = new ArrayList<>();
  }
  
  /**
   * Gets the status of this executor
   * 
   * @return the status
   */
  public AnalysisStatus getStatus() {
    return status;
  }

  public synchronized void start() {
    switch (status) {
      case CANCELLED:
        throw new IllegalStateException("Cannot restart a cancelled executer");
      case FAILED:
        throw new IllegalStateException("Cannot restart a failed executer");
      case RUNNING:
        return;
      case READY:
        status = AnalysisStatus.RUNNING;
        startExecutableModules();
    }
  }
  
  /**
   * Starts all modules whose dependencies are finished
   */
  private synchronized void startExecutableModules() {
    Iterator<ModuleExecutionState> it = scheduledModules.iterator();
    while (it.hasNext()) {
      ModuleExecutionState moduleState = it.next();
      if (moduleState.isExecutable()) {
        startModuleExecution(moduleState);
        it.remove();
      }
    }
    
    // Check if there is a dependency deadlock (there are remaining modules, but none could execute)
    if (scheduledModules.size() > 0 && runningModules.size() == 0) {
      status = AnalysisStatus.FAILED;
      Exception ex =
          new UnresolvedModuleDependencyException(
              "The module could not be executed because of a deadlock.");
      for (ModuleExecutionState module : scheduledModules) {
        failedModules.put(module.getModuleClass(), ex);
      }
    }
  }
  
  /**
   * Starts a thread executing the module
   * @param moduleState the module to execute
   */
  private synchronized void startModuleExecution(final ModuleExecutionState moduleState) {
    final ModuleResultProvider resultProvider = moduleState.getResultProvider();
    final Module<?> instance = moduleState.getInstance();

    Thread thread = new Thread(new Runnable() {
      @Override
      public void run() {
        Object result;
        try {
          result = instance.execute(resultProvider, null /* TODO  */);
        } catch(Exception e) {
          onModuleFailed(moduleState, e);
          return;
        }
        onModuleFinished(moduleState, result);
      }
    });
    thread.start();
    moduleState.setThread(thread);
    runningModules.add(moduleState);
  }
  
  private synchronized void onModuleFinished(ModuleExecutionState moduleState, Object result) {
    // Ignore results produced after failure / cancel
    if (status != AnalysisStatus.RUNNING)
      return;

    runningModules.remove(moduleState);
    for (ModuleExecutionState module : scheduledModules) {
      module.notifyDependencyFinished(moduleState.getModuleClass(), result);
    }
    startExecutableModules();
    checkFinished();
  }

  private synchronized void onModuleFailed(ModuleExecutionState moduleState, Exception e) {
    // Ignore results produced after failure / cancel
    if (status != AnalysisStatus.RUNNING)
      return;

    runningModules.remove(moduleState);
    failedModules.put(moduleState.getModuleClass(), e);
    removeModuleAndDependencies(moduleState);
    checkFinished();
  }

  private void checkFinished() {
    if (scheduledModules.size() == 0 && runningModules.size() == 0) {
      if (failedModules.isEmpty()) {
        status = AnalysisStatus.FINISHED;
      } else {
        status = AnalysisStatus.FAILED;
      }
    }
  }

  /**
   * Recursively removes the module and all modules that depend on it
   * 
   * @param moduleToRemove the module to remove
   */
  private void removeModuleAndDependencies(ModuleExecutionState moduleToRemove) {
    scheduledModules.remove(moduleToRemove);
    for (ModuleExecutionState module : scheduledModules) {
      if (module.getRemainingDependencies().contains(moduleToRemove)) {
        removeModuleAndDependencies(module);
      }
    }
  }
  
  /**
   * Stops the execution, interrupting all running modules and preventing scheduled modules from
   * starting
   */
  public void cancel() {
    if (status != AnalysisStatus.FAILED) {
      status = AnalysisStatus.CANCELLED;
    }

    for (ModuleExecutionState state : runningModules) {
      state.getThread().interrupt();
    }
    scheduledModules.clear();
  }

  /**
   * Gets a map of objects that have failed and the exceptions they have thrown
   * 
   * @return the failed exceptions; is empty if successful
   */
  public Map<ModuleClass, Exception> getFailedModules() {
    return Collections.unmodifiableMap(failedModules);
  }
}
