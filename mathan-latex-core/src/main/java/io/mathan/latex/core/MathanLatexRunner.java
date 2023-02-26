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

package io.mathan.latex.core;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.zeroturnaround.exec.ProcessExecutor;

public class MathanLatexRunner {

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

  private final MathanLatexConfiguration configuration;
  private final Build build;


  /**
   * The registry of all steps available. This registry will contain the default steps provided by the mathan-latex-maven-plugin itself and the user defined steps provided with the parameter {@link
   * MathanLatexConfiguration#getSteps()}.
   */
  private Map<String, Step> stepRegistry = new HashMap<>();

  public MathanLatexRunner(MathanLatexConfiguration configuration, Build build) {
    this.configuration = configuration;
    this.build = build;
  }

  /**
   * {@inheritDoc}
   */
  public void execute() throws LatexExecutionException {
    final List<Step> stepsToExecute = configureSteps();
    build.getLog().info("[mathan] bin directory of tex distribution: " + configuration.getTexBin());
    build.getLog().info("[mathan] output format : " + configuration.getOutputFormat());
    build.getLog().info("[mathan] latex steps: " + String.join(",", configuration.getLatexSteps()));
    build.getLog().info("[mathan] build steps: " + String.join(",", configuration.getBuildSteps()));

    File baseDirectory = build.getBasedir();
    File texDirectory = new File(baseDirectory, configuration.getSourceDirectory());

    executeSteps(stepsToExecute, texDirectory);
    // remove intermediate files
    if (!configuration.isKeepIntermediateFiles()) {
      File workingDirectory = new File(build.getBasedir(), "target/latex");
      try {
        FileUtils.deleteDirectory(workingDirectory);
      } catch (IOException e) {
        build.getLog().warn(String.format("Could not delete directory %s", workingDirectory.getAbsolutePath()));
      }
    }
  }

  /**
   * Executes the configured steps for a certain directory with a LaTeX source document. If available resources from the commons directory will be added to the execution. In this case files from the
   * source directory will overwrite files from the common directory.
   *
   * @param stepsToExecute The steps to execute.
   * @param source The directory containing the LaTeX source document.
   * @throws LatexExecutionException Most likely when an IOException occurred during the build.
   */
  private void executeSteps(List<Step> stepsToExecute, File source) throws LatexExecutionException {
    File workingDirectory = createWorkingDirectory();
    if (configuration.isEnableDependencyScan()) {
      build.resolveDependencies(workingDirectory);
    }
    copySources(source, workingDirectory);
    File mainFile = resolveMainFile(source, workingDirectory);
    build.getLog().info(String.format("[mathan] processing %s", mainFile.getName()));
    FileWriter completeLog;
    String pureName = mainFile.getName().substring(0, mainFile.getName().lastIndexOf('.'));
    completeLog = createLog(workingDirectory);
    int stepCount = stepsToExecute.size();
    for (int i = 0; i < stepCount; i++) {
      Step step = stepsToExecute.get(i);
      logHeader(completeLog, i + 1, stepCount, step);
      executeStep(step, workingDirectory, mainFile);
      appendLogTo(completeLog, workingDirectory, pureName, step);
    }
    closeLog(completeLog);
    provideArtifact(workingDirectory, pureName);
    cleanUp(workingDirectory);
  }

  private void provideArtifact(File workingDirectory, String pureName) throws LatexExecutionException {
    File outputFile = new File(workingDirectory, pureName + "." + configuration.getOutputFormat());
    try {
      File targetDirectory = new File(build.getBasedir(), "target");
      String artifactName = String.format("%s-%s.%s", build.getArtifactId(), build.getVersion(), configuration.getOutputFormat());
      File artifact = new File(targetDirectory, artifactName);
      FileUtils.copyFile(outputFile, artifact);
      build.setArtifact(artifact);
    } catch (IOException e) {
      throw new LatexExecutionException(String.format("Could not copy output file %s to target.", outputFile.getAbsolutePath()), e);
    }
  }

  private void cleanUp(File workingDirectory) {
    if (!configuration.isKeepIntermediateFiles()) {
      try {
        FileUtils.deleteDirectory(workingDirectory);
      } catch (IOException e) {
        build.getLog().warn(String.format("Could not delete directory %s", workingDirectory.getAbsolutePath()), e);
      }
    }
  }

  private File createWorkingDirectory() throws LatexExecutionException {
    File workingDirectory = new File(build.getBasedir(), "target/latex/");
    if (!workingDirectory.exists() && !workingDirectory.mkdirs()) {
      throw new LatexExecutionException(String.format("Could not create directory %s", workingDirectory.getAbsolutePath()));
    }
    return workingDirectory;
  }


  private void copySources(File source, File workingDirectory) throws LatexExecutionException {
    try {
      FileUtils.copyDirectory(source, workingDirectory);
    } catch (IOException e) {
      throw new LatexExecutionException(String.format("Could not copy context from %s to %s", source.getAbsolutePath(), workingDirectory.getAbsolutePath()));
    }
  }

  private File resolveMainFile(File source, File workingDirectory) throws LatexExecutionException {
    File mainFile;
    if (configuration.getTexFile() == null || configuration.getTexFile().isEmpty()) {
      mainFile = Utils.getFile(workingDirectory, Constants.FORMAT_TEX); //TODO: parameterize the name of the source document?
    } else {
      mainFile = new File(workingDirectory, configuration.getTexFile());
    }

    if (mainFile == null || !mainFile.exists()) {
      throw new LatexExecutionException(String.format("No LaTeX source document found in %s", source.getAbsolutePath()));
    }
    return mainFile;
  }

  private FileWriter createLog(File workingDirectory) throws LatexExecutionException {
    FileWriter completeLog;
    try {
      completeLog = new FileWriter(new File(workingDirectory, "mathan-latex-mojo.log"), true);
    } catch (IOException e) {
      throw new LatexExecutionException("Could not create mathan-latext-mojo.log", e);
    }
    return completeLog;
  }

  private void closeLog(FileWriter completeLog) throws LatexExecutionException {
    try {
      completeLog.close();
    } catch (IOException e) {
      throw new LatexExecutionException("Could not write mathan-latext-mojo.log", e);
    }
  }

  private void logHeader(FileWriter completeLog, int i, int stepCount, Step step) throws LatexExecutionException {
    try {
      completeLog.write("##################################################\n");
      completeLog.write(String.format("# Step %s/%s %s\n", i, stepCount, step.getId()));
      completeLog.write("##################################################\n");
    } catch (IOException e) {
      throw new LatexExecutionException("Could not write mathan-latext-mojo.log", e);
    }
  }

  private void appendLogTo(FileWriter completeLog, File workingDirectory, String pureName, Step step) throws LatexExecutionException {
    if (step.getLogExtension() == null) {
      return;
    }
    File stepLog = new File(workingDirectory, pureName + "." + step.getLogExtension());
    if (stepLog.exists()) {
      try {
        FileReader reader = new FileReader(stepLog);
        IOUtils.copy(reader, completeLog);
        reader.close();
        stepLog.delete();
      } catch (IOException e) {
        throw new LatexExecutionException("Could not write mathan-latext-mojo.log", e);
      }
    }
  }

  /**
   * Configures the steps to execute and checks the configuration for the build.
   *
   * @return The steps to execute.
   * @throws LatexExecutionException If the configuration is invalid.
   */
  private List<Step> configureSteps() throws LatexExecutionException {
    // check source directory
    configureSourceDirectory();
    // check output format
    configureOutputFormat();
    // setup step registry
    configureStepRegistry();
    // setup latex steps
    List<Step> listLatexSteps = configureLatexSteps();
    List<Step> listExecutables = new ArrayList<>(listLatexSteps);
    // setup build steps
    final List<Step> listBuildSteps = configureBuildSteps(listLatexSteps, listExecutables);
    // configure pre-defined steps
    configureStyleFile(Step.STEP_MAKEINDEX, configuration.getMakeIndexStyleFile());
    configureStyleFile(Step.STEP_MAKEINDEXNOMENCL, configuration.getMakeIndexNomenclStyleFile());
    // check if executables are available
    checkExecutables(listExecutables);
    return listBuildSteps;
  }

  private List<Step> configureBuildSteps(List<Step> listLatexSteps, List<Step> listExecutables) throws LatexExecutionException {
    if (configuration.getBuildSteps() == null) {
      configuration.setBuildSteps(DEFAULT_BUILD_STEPS);
    }
    List<Step> listBuildSteps = new ArrayList<>();
    for (String buildStep : configuration.getBuildSteps()) {
      if (Constants.LaTeX.equals(buildStep)) {
        listBuildSteps.addAll(listLatexSteps);
      } else {
        Step step = stepRegistry.get(buildStep);
        if (step == null) {
          throw new LatexExecutionException(String.format("Step '%s' defined in 'buildSteps' is unknown. Consider to provide the definition of the step with the configuration 'steps'.", buildStep));
        }
        listBuildSteps.add(step);
        listExecutables.add(step);
      }
    }
    return listBuildSteps;
  }

  private List<Step> configureLatexSteps() throws LatexExecutionException {
    if (configuration.getLatexSteps() == null) {
      switch (configuration.getOutputFormat()) {
        case Constants.FORMAT_DVI:
          configuration.setLatexSteps(new String[]{Step.STEP_LATEX.getId()});
          break;
        case Constants.FORMAT_PS:
          configuration.setLatexSteps(new String[]{Step.STEP_LATEX.getId(), Step.STEP_DVIPS.getId()});
          break;
        case Constants.FORMAT_PDF:
          configuration.setLatexSteps(new String[]{Step.STEP_PDFLATEX.getId()});
          break;
        default:
          throw new LatexExecutionException("Invalid output format");
      }
    }
    List<Step> listLatexSteps = new ArrayList<>();
    for (String latexStep : configuration.getLatexSteps()) {
      Step step = stepRegistry.get(latexStep);
      if (step == null) {
        throw new LatexExecutionException(String.format("Step '%s' defined in 'latexSteps' is unknown. Consider to provide the definition of the step with the configuration 'steps'.", latexStep));
      }
      listLatexSteps.add(step);
    }
    return listLatexSteps;
  }

  private void configureStepRegistry() {
    DEFAULT_EXECUTABLES.forEach(e -> stepRegistry.put(e.getId(), e));
    if (configuration.getSteps() != null) {
      Arrays.asList(configuration.getSteps()).forEach(e -> stepRegistry.put(e.getId(), e));
    }
  }

  private void configureOutputFormat() throws LatexExecutionException {
    if (configuration.getOutputFormat().length() == 0) {
      throw new LatexExecutionException("No outputFormat specified. Supported values are: dvi, pdf, ps.");
    }
    if (!Arrays.asList(Constants.FORMAT_DVI, Constants.FORMAT_PDF, Constants.FORMAT_PS).contains(configuration.getOutputFormat())) {
      throw new LatexExecutionException(String.format("Invalid outputFormat '%s' specified. Supported values are: dvi, pdf, ps.", configuration.getOutputFormat()));
    }
  }

  private void configureSourceDirectory() throws LatexExecutionException {
    File srcDir = new File(build.getBasedir(), configuration.getSourceDirectory());
    if (!srcDir.exists() || !srcDir.isDirectory()) {
      throw new LatexExecutionException(String.format("Source directory '%s' does not exist.", configuration.getSourceDirectory()));
    }
  }

  /**
   * Checks, if the given executables can be executed by finding the executables either in the configured {@link MathanLatexConfiguration#getTexBin()}  bin directory} or on PATH.
   *
   * @param listExecutables The executables to check.
   * @throws LatexExecutionException If at least one executable cannot be executed.
   */
  private void checkExecutables(List<Step> listExecutables) throws LatexExecutionException {
    List<Step> stepsToFail = listExecutables.stream().filter(step -> Utils.getExecutable(configuration.getTexBin(), step.getOperatingSystemName()) == null).collect(Collectors.toList());
    stepsToFail
        .forEach(step -> build.getLog().error(String.format("Step %s cannot be executed. Executable neither found in configured texBin '%s' nor on PATH", step.getId(), configuration.getTexBin())));
    if (!stepsToFail.isEmpty()) {
      throw new LatexExecutionException("The executable of at least one step could not be found.");
    }
  }

  /**
   * Special configuration for a step with a placeholder %style. This can be used to specify a certain style file for either {@link Step#STEP_MAKEINDEX} or @{@link Step#STEP_MAKEINDEXNOMENCL}. If a
   * style file is set, this is appended to the arguments of the executable. Otherwise no style file will be used.
   */
  private void configureStyleFile(Step step, String styleFile) {
    String arguments = step.getArguments();
    if (styleFile == null || styleFile.isEmpty()) {
      arguments = arguments.replaceAll("-s\\s+%style", "");
    } else {
      arguments = arguments.replaceAll("%style", styleFile);
    }
    step.setArguments(arguments);
  }

  /**
   * Executes a single step and executes the configured command with the specified input file. If the step is {@link Step#isOptional() is optional} the step is not executed if the input file is not
   * found. E.g. if bibtex step is executed and there are no references defined.
   *
   * @param executionStep The step to execute.
   * @param workingDirectory The working directory for the command execution.
   * @param texFile The input file to use.
   * @throws LatexExecutionException If an error occurred during the execution of the command.
   */
  private void executeStep(Step executionStep, File workingDirectory, File texFile) throws LatexExecutionException {
    File exec = Utils.getExecutable(configuration.getTexBin(), executionStep.getOperatingSystemName());
    // split command into array
    List<String> list = new ArrayList<>();
    list.add(exec.getAbsolutePath());
    Utils.tokenizeEscapedString(Step.getArguments(executionStep, texFile), list);
    String[] command = list.toArray(new String[0]);

    String prefix = "[mathan][" + executionStep.getId() + "]";

    File inputFile = Step.getInputFile(executionStep, texFile);
    int exitValue = 0;
    try {
      build.getLog().info("[mathan] execution: " + executionStep.getId());
      build.getLog().info(Arrays.toString(command));
      exitValue = new ProcessExecutor().command(command).directory(workingDirectory).redirectOutput(build.getRedirectOutput(prefix))
          .redirectError(build.getRedirectError(prefix)).destroyOnExit().execute().getExitValue();
    } catch (Exception e) {
      if (executionStep.isOptional()) {
        build.getLog().info("[mathan] execution skipped: " + executionStep.getId());
      } else {
        throw new LatexExecutionException("Building the project: ", e);
      }
    }
    if (exitValue != 0) {
      if (inputFile.exists()) {
        if (configuration.isHaltOnError()) {
          throw new LatexExecutionException(String.format("Execution of step %s failed. Process finished with exit code %s.", executionStep.getId(), exitValue));
        } else {
          build.getLog().info(String.format("[mathan] execution finished with exit code=%s: %s", exitValue, executionStep.getId()));
        }
      } else {
        build.getLog().info("[mathan] execution skipped: " + executionStep.getId());
      }
    }
  }

}
