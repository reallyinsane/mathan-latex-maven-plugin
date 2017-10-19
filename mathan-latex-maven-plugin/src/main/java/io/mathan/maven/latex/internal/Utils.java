package io.mathan.maven.latex.internal;

import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Utility class.
 *
 * @author Matthias Hanisch (reallyinsane)
 */
public class Utils {
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
                StringBuilder sb = new StringBuilder();
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
    public static File getFile(File directory, String extension) throws MojoExecutionException {
        File[] files = directory.listFiles(pathname -> pathname.getName().endsWith("." + extension));
        if (files == null || files.length == 0) {
            return null;
        } else if (files.length > 1) {
            throw new MojoExecutionException("Multiple " + extension + " files found");
        } else {
            return files[0];
        }
    }

    /**
     * Returns the File for the executable or <code>null</code> if the executable could not be found.
     * @param texBin The bin directory of the LaTeX distribution.
     * @param name The name of the executable to find.
     * @return The executable file or <code>null</code>.
     */
    public static File getExecutable(String texBin, String name) {
        File executable;
        // try to find executable in configured bin directory of the tex distribution
        if(texBin!=null&&!texBin.isEmpty()) {
            executable = new File(texBin, name);
            if(executable.exists()) {
                return executable;
            }
        }
        // check if there is a system property called texBin
        String texBinSystemProperty = System.getProperty("texBin");
        if(texBinSystemProperty!=null&&!texBinSystemProperty.isEmpty()) {
            executable = new File(texBinSystemProperty, name);
            if(executable.exists()) {
                return executable;
            }
        }

        // try to find the executable on the path
        String envPath = System.getenv("PATH");
        String[] paths = envPath.split(File.pathSeparator);
        for(String path:paths) {
            executable = new File(path, name);
            if(executable.exists()) {
                return executable;
            }
        }
        return null;
    }
}
