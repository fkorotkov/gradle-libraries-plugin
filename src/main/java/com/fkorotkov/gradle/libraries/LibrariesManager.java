package com.fkorotkov.gradle.libraries;

import com.fkorotkov.gradle.libraries.model.ProjectLibraries;
import com.fkorotkov.gradle.libraries.model.Library;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Map;

public class LibrariesManager {
  private final File dependenciesFile;

  private long lastUpdateTimestamp = 0L;
  private Map<String, Library> libraryMapping = Collections.emptyMap();

  public LibrariesManager(File dependenciesFile) {
    this.dependenciesFile = dependenciesFile;
  }

  public String get(String alias) throws FileNotFoundException {
    if (!dependenciesFile.exists()) {
      throw new IllegalStateException("dependencies.json file doesn't exist! Expected location: " + dependenciesFile.getAbsolutePath());
    }
    Map<String, Library> mapping = getLibraryMapping();
    if (!mapping.containsKey(alias)) {
      throw new IllegalStateException("dependencies.json doesn't contain information for " + alias);
    }
    return mapping.get(alias).getFullyQualifiedName();
  }

  synchronized protected Map<String, Library> getLibraryMapping() throws FileNotFoundException {
    if (dependenciesFile.lastModified() > lastUpdateTimestamp) {
      do {
        lastUpdateTimestamp = dependenciesFile.lastModified();
        libraryMapping = createMapping();
        // some extra checking that dependenciesFile hasn't changed
      } while (lastUpdateTimestamp != dependenciesFile.lastModified());
    }
    return libraryMapping;
  }

  private Map<String, Library> createMapping() throws FileNotFoundException {
    ProjectLibraries projectLibraries = ProjectLibraries.fromFile(dependenciesFile);
    return projectLibraries.getAliasMapping();
  }
}
