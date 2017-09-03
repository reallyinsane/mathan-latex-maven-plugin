package de.mathan.maven.latex;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.LogOutputStream;

@Mojo(name="latex")
public class MathanLatexMojo extends AbstractMojo {
	
	private static final String LaTeX = "LaTeX";
	private static final String[] DEFAULT_EXECUTION_CHAIN = {LaTeX, Step.STEP_BIBTEX.getId(), Step.STEP_MAKEINDEX.getId(), Step.STEP_MAKEINDEXNOMENCL.getId(), LaTeX, LaTeX};
	private static final List<Step> DEFAULT_EXECUTABLES = Arrays.asList(
			Step.STEP_BIBER, Step.STEP_BIBTEX, Step.STEP_DVIPDFM, Step.STEP_DVIPS, Step.STEP_LATEX, Step.STEP_LULATEX, Step.STEP_MAKEINDEX, Step.STEP_MAKEINDEXNOMENCL, Step.STEP_PDFLATEX, Step.STEP_PS2PDF, Step.STEP_PSLATEX, Step.STEP_XELATEX);		
	
	@Parameter(defaultValue="pdf")
	private String outputFormat;
	
	@Parameter
	private String texBin;
	
	@Parameter
	private String[] latexChain;
	
	@Parameter
	private String[] executionChain;
	
	@Parameter
	private Step[] executables;
	
	private Map<String, Step> registry = new HashMap<>();
	
	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	private MavenProject project;
	
	public MathanLatexMojo() {
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		DEFAULT_EXECUTABLES.forEach(e -> registry.put(e.getId(), e));
		if(executables!=null) {
			Arrays.asList(executables).forEach(e->registry.put(e.getId(), e));
		}
		if(executionChain==null) {
			executionChain = DEFAULT_EXECUTION_CHAIN;
		}
		getLog().debug("[mathan] execution chain: "+String.join(",", executionChain));
		if(latexChain==null) {
			switch(outputFormat) {
			case "dvi":
				latexChain = new String[] {"latex"};
				break;
			case "ps":
				latexChain = new String[] {"pslatex"};
				break;
			case "pdf":
				latexChain = new String[] {"pdflatex"};
				break;
			default:
				throw new MojoExecutionException(String.format("Unknown output format %s", outputFormat));
			}
		}
		getLog().info("[mathan] bin directory of tex distribution: "+texBin);
		getLog().info("[mathan] output format : "+outputFormat);
		getLog().debug("[mathan] LaTeX chain: "+String.join(",", latexChain));
		List<Step> latexExecutables = getExecutables(latexChain);
		List<Step> executionSteps = new ArrayList<>();
		for(String executionStep:executionChain) {
			if(LaTeX.equals(executionStep)) {
				executionSteps.addAll(latexExecutables);
			} else {
				executionSteps.addAll(getExecutables(executionStep));
			}
		}
		File baseDir = project.getBasedir();
		File texDir = new File(baseDir,"src/main/tex");
		File texFile = getFile(texDir, "tex");
		for(Step executionStep:executionSteps) {
			getLog().info("[mathan] execution: "+executionStep.getName());
			execute(executionStep, texDir, texFile);
		}
	}
	
	private void execute(Step executionStep, File texDir, File texFile) throws MojoExecutionException {
		String executableName=executionStep.getName();
        String os = System.getProperty("os.name").toLowerCase();
        if (os.indexOf("windows") >= 0) {
        	executableName+=".exe";
        }
		File exec = new File(texBin,executableName);
        // split command into array
        List<String> list = new ArrayList<>();
        list.add(exec.getAbsolutePath());
        tokenizeEscapedString(getArguments(executionStep, texFile), list);
        String[] command =  (String[]) list.toArray(new String[0]);
        
        String prefix = "[mathan]["+executionStep.getName()+"]";
        
        try {
			int exitValue = new ProcessExecutor().command(command).directory(texDir).redirectOutput(new LatexPluginLogOutputStream(getLog(), prefix)).redirectError(new LatexPluginLogOutputStream(getLog(), prefix, true)).destroyOnExit().execute().getExitValue();
		} catch (Exception e) {
			throw new MojoExecutionException("Building the project: ", e);
		}
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
			this.prefix =prefix;
			this.error = error;
		}

		@Override
		protected void processLine(String line) {
			if(error) {
				log.error(prefix+" "+line);
			} else {
				log.info(prefix+" "+line);
			}
		}
		}
	
    protected String getArguments(Step executionStep, File resource) {
        String args = executionStep.getArguments();
        if (args == null) {
            return null;
        }
        String name = resource.getName();
        String baseName = name.substring(0, resource.getName().lastIndexOf('.'));
        String inputName = baseName + "."+executionStep.getInputFormat();
        String outputName = baseName + "."+executionStep.getOutputFormat();
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

	private List<Step> getExecutables(String...steps) {
		List<Step> list = new ArrayList<>();
		for(String step:steps) {
			list.add(registry.get(step));
		}
		return list;
	}
	
	/**
	 * Splits the given string into tokens so that 
	 * sections of the string that are enclosed into quotes will
	 * form one token (without the quotes).
	 * 
	 * E.g. string = "-editor \"echo %f:%l\" -q"
	 *      tokens = { "-editor", "echo %f:%l", "-q" }
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
	        }
	        else if (token.charAt(0) == '"') {
	            StringBuffer sb = new StringBuffer();
	            sb.append(token.substring(1));
	            token = st.nextToken();
	            while (!token.endsWith("\"") && st.hasMoreTokens()) {
	                sb.append(' ');
	                sb.append(token);
	                token = st.nextToken();
	            }
	            sb.append(' ');
	            sb.append(token.substring(0, token.length()-1));
	            list.add(sb.toString());
	        } else {
	            list.add(token);
	        }
	    }
	}

	private static File getFile(File directory, String extension) throws MojoFailureException{
		File[] files = directory.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith("."+extension);
			}
		});
		if(files==null||files.length==0) {
			throw new MojoFailureException("No "+extension+" file found");
		} else if(files.length>1) {
			throw new MojoFailureException("Multiple "+extension+" files found");
		} else {
			return files[0];
		}
	}
	
}
