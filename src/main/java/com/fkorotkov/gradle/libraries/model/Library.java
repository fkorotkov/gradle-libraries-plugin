package com.fkorotkov.gradle.libraries.model;

public class Library implements Comparable<Library> {
  private String group;
  private String name;
  private String version;
  private String alias;

  public Library(String group, String name, String version) {
    this.group = group;
    this.name = name;
    this.version = version;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getGroup() {
    return group;
  }

  public String getName() {
    return name;
  }

  public String getVersion() {
    return version;
  }

  public String getRawAlias() {
    return alias;
  }

  public String getAlias() {
    if (alias != null) {
      return alias;
    }
    return getGroupName();
  }

  public String getGroupName() {
    return String.format("%s:%s", group, name);
  }

  public String getFullyQualifiedName() {
    return String.format("%s:%s:%s", group, name, version);
  }

  @Override
  public int compareTo(Library o) {
    return getFullyQualifiedName().compareTo(o.getFullyQualifiedName());
  }
}
