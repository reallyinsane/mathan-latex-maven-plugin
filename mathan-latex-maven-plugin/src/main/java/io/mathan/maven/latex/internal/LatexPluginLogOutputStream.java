package io.mathan.maven.latex.internal;

import org.apache.maven.plugin.logging.Log;
import org.zeroturnaround.exec.stream.LogOutputStream;

/**
 * Helper class for redirecting output/error from {@link org.zeroturnaround.exec.ProcessExecutor} to Maven log.
 * @author Matthias Hanisch (reallyinsane)
 */
public class LatexPluginLogOutputStream extends LogOutputStream {
    private final String prefix;
    private final Log log;
    private final boolean error;

    private LatexPluginLogOutputStream(Log log, String prefix, boolean error) {
        this.log = log;
        this.prefix = prefix;
        this.error = error;
    }

    /**
     * Creates an instance to redirect stream to maven error log.
     *
     * @param log    The maven log.
     * @param prefix The prefix to put on each line redirected.
     * @return The created instance.
     */
    public static LogOutputStream toMavenError(Log log, String prefix) {
        return new LatexPluginLogOutputStream(log, prefix, true);
    }

    /**
     * Creates an instance to redirect stream to maven debug log.
     *
     * @param log    The maven log.
     * @param prefix The prefix to put on each line redirected.
     * @return The created instance.
     */
    public static LogOutputStream toMavenDebug(Log log, String prefix) {
        return new LatexPluginLogOutputStream(log, prefix, false);
    }

    @Override
    protected void processLine(String line) {
        if (error && log.isErrorEnabled()) {
            log.error(prefix + " " + line);
        } else if (!error && log.isDebugEnabled()) {
            log.debug(prefix + " " + line);
        }
    }
}
