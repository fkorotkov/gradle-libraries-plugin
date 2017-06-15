package com.fkorotkov.gradle.libraries.tasks;

import com.fkorotkov.gradle.libraries.model.DependencyUpdatesReport;
import com.fkorotkov.gradle.libraries.model.Library;
import com.fkorotkov.gradle.libraries.model.ProjectLibraries;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.gradle.api.tasks.TaskAction;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SyncLibrariesTask extends LibrariesBaseTask {
  @TaskAction
  public void sync() throws IOException {
    DependencyUpdatesReport report = runReport();
    ProjectLibraries projectLibraries = createdUpdateLibraries(report);
    BufferedWriter dependenciesFileWriter = getDependenciesFileWriter();
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    gson.toJson(projectLibraries, dependenciesFileWriter);
    dependenciesFileWriter.close();
  }

  private ProjectLibraries createdUpdateLibraries(DependencyUpdatesReport report) {
    List<Library> allLibraries = new ArrayList<>();
    allLibraries.addAll(report.getCurrentLibraries());
    allLibraries.addAll(report.getOutdatedLibraries());

    Collections.sort(allLibraries);

    Map<String, Library> currentLibraryMapping = findCurrentProjectLibraries().getGroupNameMapping();
    for (Library updatedLibrary : allLibraries) {
      Library existingLibrary = currentLibraryMapping.get(updatedLibrary.getAlias());
      if (existingLibrary != null) {
        updatedLibrary.setAlias(existingLibrary.getRawAlias());
      }
    }

    return new ProjectLibraries(allLibraries);
  }
}
