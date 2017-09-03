package de.mathan.maven.latex;

import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

class Utils {
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
    static void tokenizeEscapedString(String args, List<String> list) {
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

    static File getFile(File directory, String extension) throws MojoExecutionException {
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

    static List<File> getSubdirectories(File texDirectory) {
        File[] files = texDirectory.listFiles(e -> e.isDirectory() && !MathanLatexMojo.DIRECTORY_COMMONS.equals(e.getName()));
        if (files == null) {
            return Collections.EMPTY_LIST;
        } else {
            return Arrays.asList(files);
        }
    }

    static File getCommonsDirectory(File texDirectory) {
        File[] files = texDirectory.listFiles(e -> e.isDirectory() && MathanLatexMojo.DIRECTORY_COMMONS.equals(e.getName()));
        if (files != null && files.length == 1) {
            return files[0];
        } else {
            return null;
        }
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
}
