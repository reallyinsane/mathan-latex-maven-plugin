package io.mathan.maven.latex;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.zeroturnaround.exec.ProcessExecutor;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The MathanLatexMojo provides the goal "latex" to generate dvi, ps or pdf out of LaTeX (.tex) documents. Therefore
 * all the LaTeX tools are executed in a defined order. There are pre-defined defaults for all supported output formats.
 * By configuration the arguments for the tool execution can be modified. It is also possible to extend the process to
 * include own tool executions.
 *
 * @author Matthias Hanisch (reallyinsane)
 */
@Mojo(name = "latex")
public class MathanLatexMojo extends AbstractMojo {

    /**
     * Identifier used as placeholder for the exeution of the latex tools to produce the output documment.
     */
    private static final String LaTeX = "LaTeX";

    /**
     * The defualt execution chain defines the order of the tool execution.
     */
    private static final String[] DEFAULT_BUILD_STEPS = {
            LaTeX, Step.STEP_BIBTEX.getId(), Step.STEP_MAKEINDEX.getId(), Step.STEP_MAKEINDEXNOMENCL.getId(), LaTeX,
            LaTeX};

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
    @Parameter(defaultValue = "pdf")
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
     * The list of tools to be executed in the build. (including bibtex, biber, makeindex, etc.). The step to create
     * the output format is set using the placeholder {@link #LaTeX}.
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
     * Parameter for controlling if intermediate files created during the build process should be kept or not. The
     * latter is the default.
     */
    @Parameter(defaultValue = "false")
    private boolean keepIntermediateFiles;

    /**
     * Parameter defining the source directory to search for LaTeX documents.
     */
    @Parameter(defaultValue = "src/main/tex")
    private String sourceDirectory;

    /**
     * Parameter defining the name of the commons dub directory inside the source directory.
     */
    @Parameter(defaultValue = "commons")
    private String commonsDirectory;

    /**
     * Parameter defining an optional index style file for makeindex.
     */
    @Parameter(defaultValue = "")
    private String makeIndexStyleFile;

    /**
     * The registry of all steps available. This registry will contain the default steps provided by the mathan-latex-maven-plugin itself
     * and the user defined steps provided with the parameter {@link #steps}.
     */
    private Map<String, Step> stepRegistry = new HashMap<>();

    public MathanLatexMojo() {
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        List<Step> stepsToExecute = configureSteps();
        getLog().info("[mathan] bin directory of tex distribution: " + texBin);
        getLog().info("[mathan] output format : " + outputFormat);
        getLog().info("[mathan] latex steps: " + String.join(",", latexSteps));
        getLog().info("[mathan] build steps: " + String.join(",", buildSteps));

        File baseDirectory = project.getBasedir();
        File texDirectory = new File(baseDirectory, sourceDirectory);

        // check if sub directories exist
        List<File> subDirectories = Utils.getSubdirectories(texDirectory, commonsDirectory);
        if (subDirectories.isEmpty()) {
            // if there are no sub directories, just one build execution is needed
            executeSteps(stepsToExecute, texDirectory, null);
        } else {
            // if there are sub directories, run a build execution for each sub directory
            for (File subDirectory : subDirectories) {
                executeSteps(stepsToExecute, subDirectory, Utils.getCommonsDirectory(texDirectory, commonsDirectory));
            }
        }
        // remove intermediate files
        if (!keepIntermediateFiles) {
            File workingDirectory = new File(project.getBasedir(), "target/latex");
            try {
                FileUtils.deleteDirectory(workingDirectory);
            } catch (IOException e) {
                getLog().warn(String.format("Could not delete directory %s", workingDirectory.getAbsolutePath()));
            }
        }
    }

    /**
     * Executes the configured steps for a certain directory with a LaTeX source document. If available resources
     * from the commons directory will be added to the execution. In this case files from the source directory will
     * overwrite files from the common directory.
     *
     * @param stepsToExecute The steps to execute.
     * @param source         The directory containing the LaTeX source document.
     * @param commons        The commons directory or <code>null</code> if no commons directory exists.
     * @throws MojoExecutionException Most likely when an IOException occurred during the build.
     */
    private void executeSteps(List<Step> stepsToExecute, File source, File commons) throws MojoExecutionException {
        File workingDirectory = new File(project.getBasedir(), "target/latex/" + source.getName());
        if (!workingDirectory.exists() && !workingDirectory.mkdirs()) {
            throw new MojoExecutionException(String.format("Could not create directory %s", workingDirectory.getAbsolutePath()));
        }
        if (commons != null) {
            try {
                FileUtils.copyDirectory(commons, workingDirectory);
            } catch (IOException e) {
                throw new MojoExecutionException(String.format("Could not copy context from %s to %s", commons.getAbsolutePath(), workingDirectory.getAbsolutePath()));
            }
        }
        try {
            FileUtils.copyDirectory(source, workingDirectory);
        } catch (IOException e) {
            throw new MojoExecutionException(String.format("Could not copy context from %s to %s", source.getAbsolutePath(), workingDirectory.getAbsolutePath()));
        }
        File texFile = Utils.getFile(workingDirectory, "tex"); //TODO: parameterize the name of the source document?
        if (texFile == null) {
            throw new MojoExecutionException(String.format("No LaTeX source document found in %s", source.getAbsolutePath()));
        }
        getLog().info(String.format("[mathan] processing %s", texFile.getName()));
        for (Step step : stepsToExecute) {
            executeStep(step, workingDirectory, texFile);
        }
        File outputFile = Utils.getFile(workingDirectory, outputFormat);
        if (outputFile != null) {
            try {
                File targetDirectory = new File(project.getBasedir(), "target");
                FileUtils.copyFileToDirectory(outputFile, targetDirectory);
                File artifact = new File(targetDirectory, outputFile.getName());
                project.getArtifact().setFile(artifact);
            } catch (IOException e) {
                throw new MojoExecutionException(String.format("Could not copy output file %s to target.", outputFile.getAbsolutePath()), e);
            }
        }
        if (!keepIntermediateFiles) {
            try {
                FileUtils.deleteDirectory(workingDirectory);
            } catch (IOException e) {
                getLog().warn(String.format("Could not delete directory %s", workingDirectory.getAbsolutePath()));
            }
        }
    }

    /**
     * Configures the steps to execute and checks the configuration for the build.
     * @return The steps to execute.
     * @throws MojoExecutionException If the configuration is invalid.
     */
    private List<Step> configureSteps() throws MojoExecutionException {
        // check source directory
        File srcDir = new File(project.getBasedir(), sourceDirectory);
        if (!srcDir.exists() || !srcDir.isDirectory()) {
            throw new MojoExecutionException(String.format("Source directory '%s' does not exist.", sourceDirectory));
        }
        // check output format
        if (outputFormat.length() == 0) {
            throw new MojoExecutionException("No outputFormat specified. Supported values are: dvi, pdf, ps.");
        }
        if (!Arrays.asList("dvi", "pdf", "ps").contains(outputFormat)) {
            throw new MojoExecutionException(String.format("Invalid outputFormat '%s' specified. Supported values are: dvi, pdf, ps.", outputFormat));
        }
        // setup step registry
        DEFAULT_EXECUTABLES.forEach(e -> stepRegistry.put(e.getId(), e));
        if (steps != null) {
            Arrays.asList(steps).forEach(e -> stepRegistry.put(e.getId(), e));
        }
        // setup latex steps
        if (latexSteps == null) {
            switch (outputFormat) {
                case "dvi":
                    latexSteps = new String[]{Step.STEP_LATEX.getId()};
                    break;
                case "ps":
                    latexSteps = new String[] {Step.STEP_LATEX.getId(), Step.STEP_DVIPS.getId()};
                    break;
                case "pdf":
                    latexSteps = new String[]{Step.STEP_PDFLATEX.getId()};
                    break;
            }
        }
        List<Step> listLatexSteps = new ArrayList<>();
        for (String latexStep : latexSteps) {
            Step step = stepRegistry.get(latexStep);
            if (step == null) {
                throw new MojoExecutionException(String.format("Step '%s' defined in 'latexSteps' is unknown. Consider to provide the definition of the step with the configuration 'steps'.", latexStep));
            }
            listLatexSteps.add(step);
        }
        // setup build steps
        if (buildSteps == null) {
            buildSteps = DEFAULT_BUILD_STEPS;
        }
        List<Step> listExecutables = new ArrayList<>(listLatexSteps);
        List<Step> listBuildSteps = new ArrayList<>();
        for (String buildStep : buildSteps) {
            if (LaTeX.equals(buildStep)) {
                listBuildSteps.addAll(listLatexSteps);
            } else {
                Step step = stepRegistry.get(buildStep);
                if (step == null) {
                    throw new MojoExecutionException(String.format("Step '%s' defined in 'buildSteps' is unknown. Consider to provide the definition of the step with the configuration 'steps'.", buildStep));
                }
                listBuildSteps.add(step);
                listExecutables.add(step);
            }
        }
        // configure pre-defined steps
        configureMakeIndex();
        // check if executables are available
        checkExecutables(listExecutables);
        return listBuildSteps;
    }

    /**
     * Checks, if the given executables can be executed by finding the executables either in the configured {@link #texBin bin directory} or
     * on PATH.
     * @param listExecutables The executables to check.
     * @throws MojoExecutionException If at least one executable cannot be executed.
     */
    private void checkExecutables(List<Step> listExecutables) throws MojoExecutionException {
        List<Step> stepsToFail = listExecutables.stream().filter(step -> Utils.getExecutable(texBin, step.getOSName()) == null).collect(Collectors.toList());
        stepsToFail.forEach(step-> getLog().error(String.format("Step %s cannot be executed. Executable neither found in configured texBin '%s' nor on PATH", step.getId(), texBin)));
        if(!stepsToFail.isEmpty()) {
            throw new MojoExecutionException("The executable of at least one step could not be found.");
        }
    }

    /**
     * Special configuration fot the step {@link Step#STEP_MAKEINDEX}. If a {@link #makeIndexStyleFile} is set, this
     * is appended to the arguments of the executable. Otherwise no makeindex style file will be used.
     */
    private void configureMakeIndex() {
        String arguments = Step.STEP_MAKEINDEX.getArguments();
        if (makeIndexStyleFile == null || makeIndexStyleFile.isEmpty()) {
            arguments = arguments.replaceAll("-s\\s+%style", "");
        } else {
            arguments = arguments.replaceAll("%style", makeIndexStyleFile);
        }
        Step.STEP_MAKEINDEX.setArguments(arguments);
    }

    /**
     * Executes a single step and executes the configured command with the specified input file. If the step is
     * {@link Step#isOptional() is optional} the step is not executed if the input file is not found. E.g. if
     * bibtex step is executed and there are no references defined.
     *
     * @param executionStep    The step to execute.
     * @param workingDirectory The working directory for the command execution.
     * @param texFile        The input file to use.
     * @throws MojoExecutionException If an error occurred during the execution of the command.
     */
    private void executeStep(Step executionStep, File workingDirectory, File texFile) throws MojoExecutionException {
        //TODO: check is step is optional && if input file is available
        String executableName = executionStep.getOSName();
        File exec = Utils.getExecutable(texBin, executionStep.getOSName());
        // split command into array
        List<String> list = new ArrayList<>();
        list.add(exec.getAbsolutePath());
        Utils.tokenizeEscapedString(Step.getArguments(executionStep, texFile), list);
        String[] command = (String[]) list.toArray(new String[0]);

        String prefix = "[mathan][" + executionStep.getId() + "]";

        File inputFile = Step.getInputFile(executionStep, texFile);
        if (inputFile.exists()) {
            int exitValue = 0;
            try {
                getLog().info("[mathan] execution: " + executionStep.getId());
                exitValue = new ProcessExecutor().command(command).directory(workingDirectory).redirectOutput(LatexPluginLogOutputStream.toMavenDebug(getLog(), prefix)).redirectError(LatexPluginLogOutputStream.toMavenError(getLog(), prefix)).destroyOnExit().execute().getExitValue();
            } catch (Exception e) {
                throw new MojoExecutionException("Building the project: ", e);
            }
            if (exitValue != 0) {
                throw new MojoExecutionException(String.format("Execution of step %s failed. Process finished with exit code %s.", executionStep.getId(), exitValue));
            }
        } else if (executionStep.isOptional()) {
            getLog().info("[mathan] execution skipped: " + executionStep.getId());
        } else {
            getLog().error(String.format("[mathan] input file %s is missing for step %s ", inputFile.getName(), executionStep.getName()));
            throw new MojoExecutionException(String.format("Input file for step %s not found", executionStep.getId()));
        }
    }

}
