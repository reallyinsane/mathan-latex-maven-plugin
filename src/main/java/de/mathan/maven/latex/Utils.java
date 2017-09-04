package de.mathan.maven.latex;

import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Utility class.
 *
 * @author Matthias Hanisch (reallyinsane)
 */
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

    /**
     * Finds a file with the given file extension in the given directory, expecting to find either one or none.
     * @param directory The directory to search a file in.
     * @param extension The file extension.
     * @return If a single file is found, the file is returned. If no file matching the file extension was found,
     * <code>null</code> is returned.
     * @throws MojoExecutionException If more than one file with the given file extension was found.
     */
    static File getFile(File directory, String extension) throws MojoExecutionException {
        File[] files = directory.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith("." + extension);
            }
        });
        if (files == null || files.length == 0) {
            return null;
        } else if (files.length > 1) {
            throw new MojoExecutionException("Multiple " + extension + " files found");
        } else {
            return files[0];
        }
    }

    /**
     * Returns the list of subdirectories of the given one. The name of the directory must not match the name
     * of the directory for common resources, {@link MathanLatexMojo#commonsDirectory}.
     *
     * @param texDirectory The directory to search sub directories for.
     * @return A list of sub directories or an empty list if there are no sub directories.
     */
    static List<File> getSubdirectories(File texDirectory, String commonsDirectory) {
        File[] files = texDirectory.listFiles(e -> e.isDirectory() && !commonsDirectory.equals(e.getName()));
        if (files == null) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(files);
        }
    }

    /**
     * Checks if the given directory contains a sub directory with name {@link MathanLatexMojo#commonsDirectory} and
     * returns it if found.
     *
     * @param texDirectory The directory to search the sub directory for.
     * @return The commons sub directory or <code>null</code> if the directory does not exists.
     */
    static File getCommonsDirectory(File texDirectory, String commonsDirectory) {
        if (commonsDirectory.isEmpty()) {
            return null;
        }
        File directory = new File(texDirectory, commonsDirectory);
        if (directory.exists()) {
            return directory;
        } else {
            return null;
        }
    }

}
