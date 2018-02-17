package io.mathan.gradle.latex;

import io.mathan.latex.core.MathanLatexConfiguration;
import org.gradle.api.file.ConfigurableFileTree;

public class MathanGradleLatexConfiguration extends MathanLatexConfiguration {

  private ConfigurableFileTree resources;

  public ConfigurableFileTree getResources() {
    return resources;
  }

  public void setResources(ConfigurableFileTree resources) {
    this.resources = resources;
  }
}
