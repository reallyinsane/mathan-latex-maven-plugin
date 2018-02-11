/*
 * Copyright 2017 Matthias Hanisch
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

package io.mathan.maven.latex;

import io.mathan.latex.core.Constants;
import io.mathan.latex.core.LatexExecutionException;
import io.mathan.latex.core.MathanLatexConfiguration;
import io.mathan.latex.core.MathanLatexRunner;
import io.mathan.latex.core.Step;
import io.mathan.maven.latex.internal.MavenBuild;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.model.fileset.FileSet;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

/**
 * The MathanLatexMojo provides the goal "latex" to generate dvi, ps or pdf out of LaTeX (.tex) documents. Therefore all the LaTeX tools are executed in a defined order. There are pre-defined defaults
 * for all supported output formats. By configuration the arguments for the tool execution can be modified. It is also possible to extend the process to include own tool executions.
 *
 * @author Matthias Hanisch (reallyinsane)
 */
@Mojo(name = "latex")
public class MathanLatexMojo extends AbstractMojo {

  private static final String[] RESOURCES_DEFAULT_EXTENSTIONS = {
      Constants.FORMAT_TEX, Constants.FORMAT_CLS, Constants.FORMAT_CLO, Constants.FORMAT_STY,
      Constants.FORMAT_BIB, Constants.FORMAT_BST, Constants.FORMAT_IDX, Constants.FORMAT_IST,
      Constants.FORMAT_GLO, Constants.FORMAT_EPS, Constants.FORMAT_PDF};

  /**
   * The defualt execution chain defines the order of the tool execution.
   */
  private static final String[] DEFAULT_BUILD_STEPS = {
      Constants.LaTeX, Step.STEP_BIBTEX.getId(), Step.STEP_MAKEINDEX.getId(), Step.STEP_MAKEINDEXNOMENCL.getId(), Constants.LaTeX,
      Constants.LaTeX};

  /**
   * This list includes the predefined execution steps supported by this plugin.
   */
  private static final List<Step> DEFAULT_EXECUTABLES = Arrays.asList(
      Step.STEP_BIBER, Step.STEP_BIBTEX, Step.STEP_DVIPDFM, Step.STEP_DVIPS, Step.STEP_LATEX, Step.STEP_LULATEX,
      Step.STEP_MAKEINDEX, Step.STEP_MAKEINDEXNOMENCL, Step.STEP_PDFLATEX, Step.STEP_PS2PDF,
      Step.STEP_XELATEX);

  /**
   * The output format. Supported are dvi, pdf and ps.
   */
  @Parameter(defaultValue = Constants.FORMAT_PDF)
  private String outputFormat;

  /**
   * The bin directory of the LaTeX distribution.
   */
  @Parameter
  private String texBin;

  /**
   * The list of tools to be executed to create the output format. (without bibtex, biber, makeindex, etc.)
   */
  @Parameter
  private String[] latexSteps;

  /**
   * The list of tools to be executed in the build. (including bibtex, biber, makeindex, etc.). The step to create the output format is set using the placeholder {@link Constants#LaTeX}.
   */
  @Parameter
  private String[] buildSteps;

  /**
   * User-defined steps which can be included in {@link #buildSteps} or {@link #latexSteps}.
   */
  @Parameter
  private Step[] steps;

  /**
   * For injecting the current maven project.
   */
  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  private MavenProject project;

  /**
   * Parameter for controlling if intermediate files created during the build process should be kept or not. The latter is the default.
   */
  @Parameter(defaultValue = "false")
  private boolean keepIntermediateFiles;

  /**
   * Parameter defining the source directory to search for LaTeX documents.
   */
  @Parameter(defaultValue = "src/main/tex")
  private String sourceDirectory;

  /**
   * Parameter defining an optional index style file for makeindex.
   */
  @Parameter
  private String makeIndexStyleFile;

  /**
   * Parameter defining an optional index style file for nomencl.
   */
  @Parameter(defaultValue = "nomencl.ist")
  private String makeIndexNomenclStyleFile;

  /**
   * The entry point to Maven Artifact Resolver, i.e. the component doing all the work.
   */
  @Component
  private RepositorySystem repoSystem;

  /**
   * The current repository/network configuration of Maven.
   */
  @Parameter(defaultValue = "${repositorySystemSession}", readonly = true, required = true)
  private RepositorySystemSession repoSession;

  /**
   * The project's remote repositories to use for the resolution.
   */
  @Parameter(defaultValue = "${project.remoteProjectRepositories}", required = true, readonly = true)
  private List<RemoteRepository> remoteRepos;

  @Parameter
  private String texFile;

  @Parameter
  private FileSet resources;

  /**
   * Parameter for controlling if build should be stopped in case the execution of a single step finished with an unexpected (non-zero) exit code. By default this parameter is set to <code>true</code>
   * but in some cases it may be useful to set it to <code>false</code>. This can be necessary if a tool finishes successfully but returns a non-zero exit code.
   */
  @Parameter(defaultValue = "true")
  private boolean haltOnError;


  /**
   * The registry of all steps available. This registry will contain the default steps provided by the mathan-latex-maven-plugin itself and the user defined steps provided with the parameter {@link
   * #steps}.
   */
  private Map<String, Step> stepRegistry = new HashMap<>();

  /**
   * {@inheritDoc}
   */
  public void execute() throws MojoExecutionException, MojoFailureException {
    configureResourcesOfDependencies();

    MathanLatexConfiguration latexConfiguration = new MathanLatexConfiguration();
    latexConfiguration.setLatexSteps(latexSteps);
    latexConfiguration.setBuildSteps(buildSteps);
    latexConfiguration.setHaltOnError(haltOnError);
    latexConfiguration.setKeepIntermediateFiles(keepIntermediateFiles);
    latexConfiguration.setMakeIndexNomenclStyleFile(makeIndexNomenclStyleFile);
    latexConfiguration.setMakeIndexStyleFile(makeIndexStyleFile);
    latexConfiguration.setOutputFormat(outputFormat);
    latexConfiguration.setSourceDirectory(sourceDirectory);
    latexConfiguration.setSteps(steps);
    latexConfiguration.setTexBin(texBin);
    latexConfiguration.setTexFile(texFile);

    MavenBuild build = new MavenBuild();
    build.setMojo(this);
    build.setProject(project);
    build.setRemoteRepos(remoteRepos);
    build.setRepoSession(repoSession);
    build.setRepoSystem(repoSystem);
    build.setResources(resources);

    MathanLatexRunner runner = new MathanLatexRunner(latexConfiguration, build);
    try {
      runner.execute();
    } catch (LatexExecutionException e) {
      throw new MojoExecutionException("Execution of Mathan LaTeX Runner failed", e);
    }
  }

  private void configureResourcesOfDependencies() {
    if (resources == null) {
      resources = new FileSet();
      for (String include : RESOURCES_DEFAULT_EXTENSTIONS) {
        resources.addInclude("**/*." + include);
      }
    }
  }
}
