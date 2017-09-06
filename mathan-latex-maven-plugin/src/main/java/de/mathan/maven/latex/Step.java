package de.mathan.maven.latex;

import java.io.File;

/**
 * This class represents a single step in an execution chain of commands during the process to generate an output
 * document for a LaTeX source document.
 *
 * @author Matthias Hanisch (reallyinsane)
 */
public class Step {

    static final Step STEP_LATEX = new Step("latex", "latex", "tex", "dvi", "-interaction=nonstopmode --src-specials %input", false);
    static final Step STEP_PDFLATEX = new Step("pdflatex", "pdflatex", "tex", "pdf", "-synctex=1 -interaction=nonstopmode --src-specials %input", false);
    static final Step STEP_XELATEX = new Step("xelatex", "xelatex", "tex", "pdf", "-synctex=1 -interaction=nonstopmode --src-specials %input", false);
    static final Step STEP_LULATEX = new Step("lulatex", "lulatex", "tex", "pdf", "-synctex=1 -interaction=nonstopmode --src-specials %input", false);
    static final Step STEP_BIBTEX = new Step("bibtex", "bibtex", "bib", "aux", "%base", true);
    static final Step STEP_BIBER = new Step("biber", "biber", "bcf", "bbl", "%input", true);
    static final Step STEP_MAKEINDEX = new Step("makeindex", "makeindex", "idx", "ind", "%input -s %style", true);
    static final Step STEP_DVIPS = new Step("dvips", "dvips", "dvi", "ps", "-R0 -o %output %input", false);
    static final Step STEP_DVIPDFM = new Step("dvipdfm", "dbipdfm", "dvi", "pdf", "%input", false);
    static final Step STEP_PS2PDF = new Step("ps2pdf", "ps2pdf", "ps", "pdf", "%input", false);
    static final Step STEP_MAKEINDEXNOMENCL = new Step("makeindexnomencl", "makeindex", "nlo", "nls", "%input -s nomencl.ist -o %output", true);


    private String id;
    private String name;
    private String arguments;
    private String inputFormat;
    private String outputFormat;
    private boolean optional;

    public Step() {

    }

    private Step(String id, String name, String inputFormat, String outputFormat, String arguments, boolean optional) {
        this.id = id;
        this.name = name;
        this.inputFormat = inputFormat;
        this.outputFormat = outputFormat;
        this.arguments = arguments;
        this.optional = optional;
    }

    static File getInputFile(Step step, File texFile) {
        return new File(texFile.getParent(), texFile.getName().substring(0, texFile.getName().indexOf(".tex")) + "." + step.getInputFormat());
    }

    static String getArguments(Step executionStep, File resource) {
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

    String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String getArguments() {
        return arguments;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    String getInputFormat() {
        return inputFormat;
    }

    public void setInputFormat(String inputFormat) {
        this.inputFormat = inputFormat;
    }

    String getOutputFormat() {
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

    String getOSName() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            return name+".exe";
        } else {
            return name;
        }
    }

}