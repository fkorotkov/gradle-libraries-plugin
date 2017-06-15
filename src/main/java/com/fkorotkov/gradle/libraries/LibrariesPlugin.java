package com.fkorotkov.gradle.libraries;

import com.fkorotkov.gradle.libraries.tasks.UpdateLibrariesTask;
import com.fkorotkov.gradle.libraries.tasks.SyncLibrariesTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

public class LibrariesPlugin implements Plugin<Project> {
  // to make sure there is one manager per root project to not re-parse json over and over again
  private static volatile ConcurrentHashMap<Project, LibrariesManager> librariesManagers = new ConcurrentHashMap<>();

  @Override
  public void apply(Project project) {
    project.getExtensions().add("libraries", getManager(project.getRootProject()));
    if (project == project.getRootProject()) {
      project.getTasks().create("syncLibraries", SyncLibrariesTask.class);
      project.getTasks().create("updateLibraries", UpdateLibrariesTask.class);
    }
  }

  private static LibrariesManager getManager(Project project) {
    File dependenciesFile = new File(project.getRootDir(), "dependencies.json");
    librariesManagers.putIfAbsent(project, new LibrariesManager(dependenciesFile));
    return librariesManagers.get(project);
  }
}
