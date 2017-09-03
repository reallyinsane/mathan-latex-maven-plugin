package de.mathan.maven.latex;

public class Step {
	
	public static final Step STEP_LATEX = new Step("latex", "latex", "tex", "dvi", "-interaction=nonstopmode --src-specials %input", false);
	public static final Step STEP_PSLATEX = new Step("pslatex", "pslatex", "tex", "ps", "-interaction=nonstopmode --src-specials %input", false);
	public static final Step STEP_PDFLATEX = new Step("pdflatex", "pdflatex", "tex", "pdf", "-synctex=1 -interaction=nonstopmode --src-specials %input", false);
	public static final Step STEP_XELATEX = new Step("xelatex", "xelatex", "tex", "pdf", "-synctex=1 -interaction=nonstopmode --src-specials %input", false);
	public static final Step STEP_LULATEX = new Step("lulatex", "lulatex", "tex", "pdf", "-synctex=1 -interaction=nonstopmode --src-specials %input", false);
	public static final Step STEP_BIBTEX = new Step("bibtex", "bibtex", "bib", "aux", "%base", true);
	public static final Step STEP_BIBER = new Step("biber", "biber", "bcf", "bbl", "%input", true);
	public static final Step STEP_MAKEINDEX = new Step("makeindex", "makeindex", "idx", "ind", "%input -s %style", true);
	public static final Step STEP_DVIPS = new Step("dvips", "dvips", "dvi", "ps", "-R0 -o %output %input", false);
	public static final Step STEP_DVIPDFM = new Step("dvipdfm", "dbipdfm", "dvi", "pdf", "%input", false);
	public static final Step STEP_PS2PDF = new Step("ps2pdf", "ps2pdf", "ps", "pdf", "%input", false);
	public static final Step STEP_MAKEINDEXNOMENCL = new Step("makeindexnomencl", "makeindex", "nlo", "nls", "%input -s nomencl.ist -o %output", true);			
	
	
	private String id;
	private String name;
	private String arguments;
	private String inputFormat;
	private String outputFormat;
	private boolean optional;
	
	public Step() {
		
	}
	
	public Step(String id, String name, String inputFormat, String outputFormat, String arguments, boolean optional) {
		this.id = id;
		this.name = name;
		this.inputFormat = inputFormat;
		this.outputFormat = outputFormat;
		this.arguments = arguments;
		this.optional = optional;
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

	public String getInputFormat() {
		return inputFormat;
	}

	public void setInputFormat(String inputFormat) {
		this.inputFormat = inputFormat;
	}

	public String getOutputFormat() {
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
	

}