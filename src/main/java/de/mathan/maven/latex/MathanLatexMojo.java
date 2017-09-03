package de.mathan.maven.latex;

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
     * Identifier for a sub directory with common resources for all tex documents.
     */
    static final String DIRECTORY_COMMONS = "commons";

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
            Step.STEP_MAKEINDEX, Step.STEP_MAKEINDEXNOMENCL, Step.STEP_PDFLATEX, Step.STEP_PS2PDF, Step.STEP_PSLATEX,
            Step.STEP_XELATEX);

    /**
     * The output format. Supported are dvi, pdf and ps.
     */
    @Parameter(defaultValue = "pdf")
    private String outputFormat;

    /**
     * The bin directory of the LaTeX distribution.
     */
    @Parameter(required = true)
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

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

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
        File texDirectory = new File(baseDirectory, "src/main/tex");

        List<File> subDirectories = Utils.getSubdirectories(texDirectory);
        File commonsDirectory = Utils.getCommonsDirectory(texDirectory);
        if (subDirectories.isEmpty()) {
            execute(stepsToExecute, texDirectory, null);
        } else {
            for (File subDirectory : subDirectories) {
                execute(stepsToExecute, subDirectory, commonsDirectory);
            }
        }
    }

    private void execute(List<Step> stepsToExecute, File source, File commons) throws MojoExecutionException {
        File targetDirectory = new File(project.getBasedir(), "target/latex/" + source.getName());
        if (!targetDirectory.mkdirs()) {
            throw new MojoExecutionException(String.format("Could not create directory %s", targetDirectory.getAbsolutePath()));
        }
        if (commons != null) {
            try {
                FileUtils.copyDirectory(commons, targetDirectory);
            } catch (IOException e) {
                throw new MojoExecutionException(String.format("Could not copy context from %s to %s", commons.getAbsolutePath(), targetDirectory.getAbsolutePath()));
            }
        }
        try {
            FileUtils.copyDirectory(source, targetDirectory);
        } catch (IOException e) {
            throw new MojoExecutionException(String.format("Could not copy context from %s to %s", source.getAbsolutePath(), targetDirectory.getAbsolutePath()));
        }
        File texFile = Utils.getFile(targetDirectory, "tex");
        getLog().info(String.format("[mathan] processing %s", texFile.getName()));
        for (Step step : stepsToExecute) {
            getLog().info("[mathan] execution: " + step.getName());
            execute(step, targetDirectory, texFile);
        }
        File outputFile = Utils.getFile(targetDirectory, outputFormat);
        try {
            FileUtils.copyFileToDirectory(outputFile, new File(project.getBasedir(), "target"));
        } catch (IOException e) {
            throw new MojoExecutionException(String.format("Could not copy output file %s to target.", outputFile.getAbsolutePath()), e);
        }
        try {
            FileUtils.deleteDirectory(targetDirectory);
        } catch (IOException e) {
            getLog().warn(String.format("Could not delete directory %s", targetDirectory.getAbsolutePath()));
        }
    }

    private List<Step> configureSteps() throws MojoExecutionException {
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
                    latexSteps = new String[]{Step.STEP_PSLATEX.getId()};
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
                throw new MojoExecutionException(String.format("Step '%s' defined in 'latexSteps' is unknown. Consider to provide the definition of the step with the configuration 'steps'."));
            }
            listLatexSteps.add(step);
        }
        // setup build steps
        if (buildSteps == null) {
            buildSteps = DEFAULT_BUILD_STEPS;
        }
        List<Step> listBuildSteps = new ArrayList<>();
        for (String buildStep : buildSteps) {
            if (LaTeX.equals(buildStep)) {
                listBuildSteps.addAll(listLatexSteps);
            } else {
                Step step = stepRegistry.get(buildStep);
                if (step == null) {
                    throw new MojoExecutionException(String.format("Step '%s' defined in 'buildSteps' is unknown. Consider to provide the definition of the step with the configuration 'steps'."));
                }
                listBuildSteps.add(step);
            }
        }
        return listBuildSteps;
    }

    private void execute(Step executionStep, File texDir, File texFile) throws MojoExecutionException {
        String executableName = executionStep.getName();
        String os = System.getProperty("os.name").toLowerCase();
        if (os.indexOf("windows") >= 0) {
            executableName += ".exe";
        }
        File exec = new File(texBin, executableName);
        // split command into array
        List<String> list = new ArrayList<>();
        list.add(exec.getAbsolutePath());
        Utils.tokenizeEscapedString(Utils.getArguments(executionStep, texFile), list);
        String[] command = (String[]) list.toArray(new String[0]);

        String prefix = "[mathan][" + executionStep.getName() + "]";

        try {
            int exitValue = new ProcessExecutor().command(command).directory(texDir).redirectOutput(new LatexPluginLogOutputStream(getLog(), prefix)).redirectError(new LatexPluginLogOutputStream(getLog(), prefix, true)).destroyOnExit().execute().getExitValue();
        } catch (Exception e) {
            throw new MojoExecutionException("Building the project: ", e);
        }
    }

}
