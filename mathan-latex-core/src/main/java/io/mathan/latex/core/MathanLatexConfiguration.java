/*
 * Copyright 2018 Matthias Hanisch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.mathan.latex.core;

public class MathanLatexConfiguration {

  /**
   * The output format. Supported are dvi, pdf and ps.
   */
  private String outputFormat = Constants.FORMAT_PDF;

  /**
   * The bin directory of the LaTeX distribution.
   */
  private String texBin;

  /**
   * The list of tools to be executed to create the output format. (without bibtex, biber, makeindex, etc.)
   */
  private String[] latexSteps;

  /**
   * The list of tools to be executed in the build. (including bibtex, biber, makeindex, etc.). The step to create the output format is set using the placeholder {@link Constants#LaTeX}.
   */
  private String[] buildSteps;

  /**
   * User-defined steps which can be included in {@link #buildSteps} or {@link #latexSteps}.
   */
  private Step[] steps;

  /**
   * Parameter for controlling if intermediate files created during the build process should be kept or not. The latter is the default.
   */
  private boolean keepIntermediateFiles = false;

  /**
   * Parameter defining the source directory to search for LaTeX documents.
   */
  private String sourceDirectory = "src/main/tex";

  /**
   * Parameter defining an optional index style file for makeindex.
   */
  private String makeIndexStyleFile;

  /**
   * Parameter defining an optional index style file for nomencl.
   */
  private String makeIndexNomenclStyleFile = "nomencl.ist";

  private String texFile;

  private boolean enableDependencyScan = false;

  /**
   * Parameter for controlling if build should be stopped in case the execution of a single step finished with an unexpected (non-zero) exit code. By default this parameter is set to <code>true</code>
   * but in some cases it may be useful to set it to <code>false</code>. This can be necessary if a tool finishes successfully but returns a non-zero exit code.
   */
  private boolean haltOnError = true;

  public String getOutputFormat() {
    return outputFormat;
  }

  public void setOutputFormat(String outputFormat) {
    this.outputFormat = outputFormat;
  }

  public String getTexBin() {
    return texBin;
  }

  public void setTexBin(String texBin) {
    this.texBin = texBin;
  }

  public String[] getLatexSteps() {
    return latexSteps;
  }

  public void setLatexSteps(String[] latexSteps) {
    this.latexSteps = latexSteps;
  }

  public String[] getBuildSteps() {
    return buildSteps;
  }

  public void setBuildSteps(String[] buildSteps) {
    this.buildSteps = buildSteps;
  }

  public Step[] getSteps() {
    return steps;
  }

  public void setSteps(Step[] steps) {
    this.steps = steps;
  }

  public boolean isKeepIntermediateFiles() {
    return keepIntermediateFiles;
  }

  public void setKeepIntermediateFiles(boolean keepIntermediateFiles) {
    this.keepIntermediateFiles = keepIntermediateFiles;
  }

  public String getSourceDirectory() {
    return sourceDirectory;
  }

  public void setSourceDirectory(String sourceDirectory) {
    this.sourceDirectory = sourceDirectory;
  }

  public String getMakeIndexStyleFile() {
    return makeIndexStyleFile;
  }

  public void setMakeIndexStyleFile(String makeIndexStyleFile) {
    this.makeIndexStyleFile = makeIndexStyleFile;
  }

  public String getMakeIndexNomenclStyleFile() {
    return makeIndexNomenclStyleFile;
  }

  public void setMakeIndexNomenclStyleFile(String makeIndexNomenclStyleFile) {
    this.makeIndexNomenclStyleFile = makeIndexNomenclStyleFile;
  }

  public String getTexFile() {
    return texFile;
  }

  public void setTexFile(String texFile) {
    this.texFile = texFile;
  }

  public boolean isHaltOnError() {
    return haltOnError;
  }

  public void setHaltOnError(boolean haltOnError) {
    this.haltOnError = haltOnError;
  }

  public boolean isEnableDependencyScan() {
    return enableDependencyScan;
  }

  public void setEnableDependencyScan(boolean enableDependencyScan) {
    this.enableDependencyScan = enableDependencyScan;
  }
}
