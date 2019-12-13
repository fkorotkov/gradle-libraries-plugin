package com.fkorotkov.gradle.libraries.tasks;

import com.fkorotkov.gradle.libraries.model.ProjectLibraries;
import com.github.benmanes.gradle.versions.updates.resolutionstrategy.ResolutionStrategyWithCurrent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.fkorotkov.gradle.libraries.model.DependencyUpdatesReport;
import com.fkorotkov.gradle.libraries.model.Library;
import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;
import org.gradle.util.ConfigureUtil;
import org.gradle.util.SingleMessageLogger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

public class UpdateLibrariesTask extends LibrariesBaseTask {
  @Internal
  Closure resolutionStrategy = null;
  private Action<? super ResolutionStrategyWithCurrent> resolutionStrategyAction = null;

  @TaskAction
  public void update() throws IOException {
    getProject().evaluationDependsOnChildren();

    if (resolutionStrategy != null) {
      resolutionStrategy(ConfigureUtil.configureUsing(resolutionStrategy));
    }

    DependencyUpdatesReport report = runReport();
    ProjectLibraries projectLibraries = updateLibraries(report);
    BufferedWriter dependenciesFileWriter = getDependenciesFileWriter();
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    gson.toJson(projectLibraries, dependenciesFileWriter);
    dependenciesFileWriter.close();
  }

  private ProjectLibraries updateLibraries(DependencyUpdatesReport report) {
    Map<String, String> updatesMapping = report.findUpdates();

    ProjectLibraries projectLibraries = findCurrentProjectLibraries();
    for (Library library : projectLibraries.libraries) {
      String availableVersionUpdate = updatesMapping.get(library.getGroupName());
      if (availableVersionUpdate != null) {
        library.setVersion(availableVersionUpdate);
      }
    }
    return projectLibraries;
  }

  /**
   * Sets the {@link #resolutionStrategy} to the provided strategy.
   * @param resolutionStrategy the resolution strategy
   */
  void resolutionStrategy(final Action<? super ResolutionStrategyWithCurrent> resolutionStrategy) {
    this.resolutionStrategyAction = resolutionStrategy;
    this.resolutionStrategy = null;
  }
}
