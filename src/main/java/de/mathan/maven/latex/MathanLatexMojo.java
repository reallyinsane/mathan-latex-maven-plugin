package de.mathan.maven.latex;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.LogOutputStream;

import java.io.File;
import java.io.FileFilter;
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
    private static final String DIRECTORY_COMMONS = "commons";

    /**
     * The defualt execution chain defines the order of the tool execution.
     */
    private static final String[] DEFAULT_EXECUTION_CHAIN = {
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

    private Map<String, Step> stepRegistry = new HashMap<>();

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    public MathanLatexMojo() {
    }

    /**
     * Splits the given string into tokens so that
     * sections of the string that are enclosed into quotes will
     * form one token (without the quotes).
     * <p>
     * E.g. string = "-editor \"echo %f:%l\" -q"
     * tokens = { "-editor", "echo %f:%l", "-q" }
     *
     * @param args the string
     * @param list tokens will be added to the end of this list
     *             in the order they are extracted
     */
    public static void tokenizeEscapedString(String args, List<String> list) {
        StringTokenizer st = new StringTokenizer(args, " ");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.charAt(0) == '"' && token.charAt(token.length() - 1) == '"') {
                list.add(token.substring(1, token.length() - 1));
            } else if (token.charAt(0) == '"') {
                StringBuffer sb = new StringBuffer();
                sb.append(token.substring(1));
                token = st.nextToken();
                while (!token.endsWith("\"") && st.hasMoreTokens()) {
                    sb.append(' ');
                    sb.append(token);
                    token = st.nextToken();
                }
                sb.append(' ');
                sb.append(token.substring(0, token.length() - 1));
                list.add(sb.toString());
            } else {
                list.add(token);
            }
        }
    }

    private static File getFile(File directory, String extension) throws MojoExecutionException {
        File[] files = directory.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith("." + extension);
            }
        });
        if (files == null || files.length == 0) {
            throw new MojoExecutionException("No " + extension + " file found");
        } else if (files.length > 1) {
            throw new MojoExecutionException("Multiple " + extension + " files found");
        } else {
            return files[0];
        }
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        List<Step> stepsToExecute = configureSteps();
        getLog().info("[mathan] bin directory of tex distribution: " + texBin);
        getLog().info("[mathan] output format : " + outputFormat);
        getLog().info("[mathan] latex steps: " + String.join(",", latexSteps));
        getLog().info("[mathan] build steps: " + String.join(",", buildSteps));

        File baseDirectory = project.getBasedir();
        File texDirectory = new File(baseDirectory, "src/main/tex");

        List<File> subDirectories = getSubdirectories(texDirectory);
        File commonsDirectory = getCommonsDirectory(texDirectory);
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
        File texFile = getFile(targetDirectory, "tex");
        getLog().info(String.format("[mathan] processing %s", texFile.getName()));
        for (Step step : stepsToExecute) {
            getLog().info("[mathan] execution: " + step.getName());
            execute(step, targetDirectory, texFile);
        }
    }

    private List<File> getSubdirectories(File texDirectory) {
        File[] files = texDirectory.listFiles(e -> e.isDirectory() && !DIRECTORY_COMMONS.equals(e.getName()));
        if (files == null) {
            return Collections.EMPTY_LIST;
        } else {
            return Arrays.asList(files);
        }
    }

    private File getCommonsDirectory(File texDirectory) {
        File[] files = texDirectory.listFiles(e -> e.isDirectory() && DIRECTORY_COMMONS.equals(e.getName()));
        if (files != null && files.length == 1) {
            return files[0];
        } else {
            return null;
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
        tokenizeEscapedString(getArguments(executionStep, texFile), list);
        String[] command = (String[]) list.toArray(new String[0]);

        String prefix = "[mathan][" + executionStep.getName() + "]";

        try {
            int exitValue = new ProcessExecutor().command(command).directory(texDir).redirectOutput(new LatexPluginLogOutputStream(getLog(), prefix)).redirectError(new LatexPluginLogOutputStream(getLog(), prefix, true)).destroyOnExit().execute().getExitValue();
        } catch (Exception e) {
            throw new MojoExecutionException("Building the project: ", e);
        }
    }

    protected String getArguments(Step executionStep, File resource) {
        String args = executionStep.getArguments();
        if (args == null) {
            return null;
        }
        String name = resource.getName();
        String baseName = name.substring(0, resource.getName().lastIndexOf('.'));
        String inputName = baseName + "." + executionStep.getInputFormat();
        String outputName = baseName + "." + executionStep.getOutputFormat();
        if (baseName.indexOf(' ') >= 0) {
            inputName = "\"" + inputName + "\"";
            outputName = "\"" + outputName + "\"";
        }

        if (args.indexOf("%input") >= 0) {
            args = args.replaceAll("%input", inputName);
        }
        if (args.indexOf("%base") >= 0) {
            args = args.replaceAll("%base", baseName);
        }
        if (args.indexOf("%output") >= 0) {
            args = args.replaceAll("%output", outputName);
        }
        return args;
    }

    private class LatexPluginLogOutputStream extends LogOutputStream {
        private final String prefix;
        private final Log log;
        private final boolean error;

        LatexPluginLogOutputStream(Log log, String prefix) {
            this(log, prefix, false);
        }

        LatexPluginLogOutputStream(Log log, String prefix, boolean error) {
            this.log = log;
            this.prefix = prefix;
            this.error = error;
        }

        @Override
        protected void processLine(String line) {
            if (error) {
                log.error(prefix + " " + line);
            } else {
                log.info(prefix + " " + line);
            }
        }
    }

}
