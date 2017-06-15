package com.fkorotkov.gradle.libraries.model;

public class LibraryWithUpdate extends Library {
  public String updateVersion;

  public LibraryWithUpdate(String group, String name, String version, String release) {
    super(group, name, version);
    updateVersion = release;
  }

  public String getUpdateVersion() {
    return updateVersion;
  }
}
