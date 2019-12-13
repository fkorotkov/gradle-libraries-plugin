package com.fkorotkov.gradle.libraries.tasks;

import com.fkorotkov.gradle.libraries.model.ProjectLibraries;
import com.fkorotkov.gradle.libraries.model.DependencyUpdatesReport;
import com.github.benmanes.gradle.versions.updates.DependencyUpdates;
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesReporter;
import com.github.benmanes.gradle.versions.updates.resolutionstrategy.ResolutionStrategyWithCurrent;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.Input;

import java.io.*;

public class LibrariesBaseTask extends DefaultTask {
  @Input
  public String revision = "release";

  public Action<? super ResolutionStrategyWithCurrent> resolutionStrategy = null;

  protected BufferedWriter getDependenciesFileWriter() throws IOException {
    return new BufferedWriter(new FileWriter(getDependenciesFile()));
  }

  protected File getDependenciesFile() {
    return new File(getProject().getRootProject().getRootDir(), "dependencies.json");
  }

  protected ProjectLibraries findCurrentProjectLibraries() {
    try {
      return ProjectLibraries.fromFile(getDependenciesFile());
    } catch (FileNotFoundException e) {
      return new ProjectLibraries();
    }
  }

  protected DependencyUpdatesReport runReport() throws FileNotFoundException {
    getProject().evaluationDependsOnChildren();
    Project rootProject = getProject().getRootProject();
    DependencyUpdates evaluator = new DependencyUpdates(rootProject, resolutionStrategy, revision);
    DependencyUpdatesReporter reporter = evaluator.run();
    return DependencyUpdatesReport.fromReporter(reporter);
  }
}
