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
import org.gradle.api.tasks.OutputDirectories;
import org.gradle.api.tasks.OutputFile;

import java.io.*;

public abstract class LibrariesBaseTask extends DefaultTask {
  private String revision = "release";

  @Input
  public String getRevision() {
    return revision;
  }

  public void setRevision(String revision) {
    this.revision = revision;
  }

  public Action<? super ResolutionStrategyWithCurrent> resolutionStrategy = null;

  @OutputFile
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
    DependencyUpdates evaluator = new DependencyUpdates(rootProject, resolutionStrategy, getRevision());
    DependencyUpdatesReporter reporter = evaluator.run();
    return DependencyUpdatesReport.fromReporter(reporter);
  }
}
