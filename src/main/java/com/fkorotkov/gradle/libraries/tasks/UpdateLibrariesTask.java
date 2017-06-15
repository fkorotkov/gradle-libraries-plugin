package com.fkorotkov.gradle.libraries.tasks;

import com.fkorotkov.gradle.libraries.model.ProjectLibraries;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.fkorotkov.gradle.libraries.model.DependencyUpdatesReport;
import com.fkorotkov.gradle.libraries.model.Library;
import org.gradle.api.tasks.TaskAction;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

public class UpdateLibrariesTask extends LibrariesBaseTask {
  @TaskAction
  public void update() throws IOException {
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
}
