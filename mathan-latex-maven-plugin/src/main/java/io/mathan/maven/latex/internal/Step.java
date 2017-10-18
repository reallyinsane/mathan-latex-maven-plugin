package io.mathan.maven.latex.internal;

import java.io.File;

/**
 * This class represents a single step in an execution chain of commands during the process to generate an output
 * document for a LaTeX source document.
 *
 * @author Matthias Hanisch (reallyinsane)
 */
public class Step {

    public static final Step STEP_LATEX = new Step("latex", "latex", Constants.FORMAT_TEX, Constants.FORMAT_DVI, "-interaction=nonstopmode --src-specials %input", false, "log");
    public static final Step STEP_PDFLATEX = new Step("pdflatex", "pdflatex", Constants.FORMAT_TEX, Constants.FORMAT_PDF, "-synctex=1 -interaction=nonstopmode --src-specials %input", false, "log");
    public static final Step STEP_XELATEX = new Step("xelatex", "xelatex", Constants.FORMAT_TEX, Constants.FORMAT_PDF, "-synctex=1 -interaction=nonstopmode --src-specials %input", false, "log");
    public static final Step STEP_LULATEX = new Step("lulatex", "lulatex", Constants.FORMAT_TEX, Constants.FORMAT_PDF, "-synctex=1 -interaction=nonstopmode --src-specials %input", false, "log");
    public static final Step STEP_BIBTEX = new Step("bibtex", "bibtex", Constants.FORMAT_BIB, Constants.FORMAT_AUX, "%base", true, "blg");
    public static final Step STEP_BIBER = new Step("biber", "biber", Constants.FORMAT_BCF, Constants.FORMAT_BBL, "%input", true, "blg");
    public static final Step STEP_MAKEINDEX = new Step("makeindex", "makeindex", Constants.FORMAT_IDX, "ind", "%input -s %style", true, "ilg");
    public static final Step STEP_DVIPS = new Step("dvips", "dvips", Constants.FORMAT_DVI, Constants.FORMAT_PS, "-R0 -o %output %input", false, "log");
    public static final Step STEP_DVIPDFM = new Step("dvipdfm", "dbipdfm", Constants.FORMAT_DVI, Constants.FORMAT_PDF, "%input", false, "log");
    public static final Step STEP_PS2PDF = new Step("ps2pdf", "ps2pdf", Constants.FORMAT_PS, Constants.FORMAT_PDF, "%input", false, "log");
    public static final Step STEP_MAKEINDEXNOMENCL = new Step("makeindexnomencl", "makeindex", Constants.FORMAT_NLO, Constants.FORMAT_NLS, "%input -s nomencl.ist -o %output", true, "ilg");


    /**
     * A unique id.
     */
    private String id;
    /**
     * The name of the executable to run.
     */
    private String name;
    /**
     * The arguments for the executable. The following placeholders can be used:
     * <ul>
     *     <li>%input: The name of the input file (including file extension)</li>
     *     <li>%output: The name of the output file (including file extension)</li>
     *     <li>%base: The name of the input file (without file extension)</li>
     * </ul>
     */
    private String arguments;
    /**
     * File extension of the input format.
     */
    private String inputFormat;
    /**
     * File extension of the output format.
     */
    private String outputFormat;
    /**
     * Flag indicating if the step is optional. The step may be skipped if the required input file does not exist. (e.g. bibtex)
     */
    private boolean optional;

    /**
     * Extension specifying the log file produces with this step.
     */
    private String logExtension;


    public Step() {

    }

    private Step(String id, String name, String inputFormat, String outputFormat, String arguments, boolean optional, String logExtension) {
        this.id = id;
        this.name = name;
        this.inputFormat = inputFormat;
        this.outputFormat = outputFormat;
        this.arguments = arguments;
        this.optional = optional;
        this.logExtension = logExtension;
    }

    public static File getInputFile(Step step, File texFile) {
        return new File(texFile.getParent(), texFile.getName().substring(0, texFile.getName().indexOf(".tex")) + "." + step.getInputFormat());
    }

    public static String getArguments(Step executionStep, File resource) {
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

        args = args.replaceAll("%input", inputName);
        args = args.replaceAll("%base", baseName);
        args = args.replaceAll("%output", outputName);
        return args;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArguments() {
        return arguments;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    private String getInputFormat() {
        return inputFormat;
    }

    public void setInputFormat(String inputFormat) {
        this.inputFormat = inputFormat;
    }

    private String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public String getLogExtension() { return this.logExtension; }

    public String getOSName() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            return name+".exe";
        } else {
            return name;
        }
    }

}