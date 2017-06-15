package com.fkorotkov.gradle.libraries.model;

import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectLibraries {
  public static ProjectLibraries fromFile(File file) throws FileNotFoundException {
    Reader reader = new BufferedReader(new FileReader(file));
    return new Gson().fromJson(reader, ProjectLibraries.class);
  }

  public List<Library> libraries;

  public ProjectLibraries() {
    this(new ArrayList<>());
  }

  public ProjectLibraries(List<Library> libraries) {
    this.libraries = libraries;
  }

  public Map<String, Library> getAliasMapping() {
    Map<String, Library> result = new HashMap<>();
    for (Library library : libraries) {
      result.put(library.getAlias(), library);
    }
    return result;
  }

  public Map<String, Library> getGroupNameMapping() {
    Map<String, Library> result = new HashMap<>();
    for (Library library : libraries) {
      result.put(library.getGroupName(), library);
    }
    return result;
  }
}
