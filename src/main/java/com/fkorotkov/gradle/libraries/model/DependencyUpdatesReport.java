package com.fkorotkov.gradle.libraries.model;

import com.github.benmanes.gradle.versions.reporter.result.Dependency;
import com.github.benmanes.gradle.versions.reporter.result.DependencyOutdated;
import com.github.benmanes.gradle.versions.reporter.result.Result;
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesReporter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DependencyUpdatesReport {
  private List<Library> currentLibraries = new ArrayList<>();
  private List<LibraryWithUpdate> outdatedLibraries = new ArrayList<>();

  public static DependencyUpdatesReport fromReporter(DependencyUpdatesReporter reporter) throws FileNotFoundException {
    Result report = reporter.buildBaseObject();
    final DependencyUpdatesReport result = new DependencyUpdatesReport();
    for (Dependency dependency : report.getCurrent().getDependencies()) {
      Library library = new Library(dependency.getGroup(), dependency.getName(), dependency.getVersion());
      result.addCurrentLibrary(library);
    }
    for (DependencyOutdated dependency : report.getOutdated().getDependencies()) {
      LibraryWithUpdate library = new LibraryWithUpdate(
        dependency.getGroup(),
        dependency.getName(),
        dependency.getVersion(),
        dependency.getAvailable().getRelease()
      );
      result.addOutdatedLibrary(library);
    }
    return result;
  }

  public List<Library> getCurrentLibraries() {
    return currentLibraries;
  }

  public List<LibraryWithUpdate> getOutdatedLibraries() {
    return outdatedLibraries;
  }

  public void addCurrentLibrary(Library library) {
    currentLibraries.add(library);
  }

  private void addOutdatedLibrary(LibraryWithUpdate library) {
    outdatedLibraries.add(library);
  }

  public Map<String, String> findUpdates() {
    Map<String, String> result = new HashMap<>();
    for (LibraryWithUpdate library : outdatedLibraries) {
      result.put(library.getGroupName(), library.getUpdateVersion());
    }
    return result;
  }
}
