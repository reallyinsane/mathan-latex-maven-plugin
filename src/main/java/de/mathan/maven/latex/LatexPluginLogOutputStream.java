package de.mathan.maven.latex;

import org.apache.maven.plugin.logging.Log;
import org.zeroturnaround.exec.stream.LogOutputStream;

class LatexPluginLogOutputStream extends LogOutputStream {
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
