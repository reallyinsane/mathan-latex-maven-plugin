package io.mathan.maven.it;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.maven.shared.utils.cli.CommandLineException;
import org.apache.maven.shared.utils.cli.CommandLineUtils;
import org.apache.maven.shared.utils.cli.Commandline;
import org.apache.maven.shared.utils.cli.StreamConsumer;
import org.apache.maven.shared.utils.cli.WriterStreamConsumer;
import org.apache.maven.shared.utils.io.IOUtil;

public abstract class Verifier {

  private final String LOG_FILENAME = "verifier.log";

  final String baseDirectory;
  final Options options;

  Verifier(String baseDirectory, Options options) {
    this.baseDirectory = baseDirectory;
    this.options = options;
  }

  protected abstract Commandline createCommandline();

  public int execute(String action) throws VerifierException {
    Commandline cmd = createCommandline();
    processOptions(cmd);
    cmd.createArg().setValue(action);
    Writer logWriter = null;
    try {
      logWriter = new FileWriter(new File(baseDirectory, LOG_FILENAME));
      StreamConsumer out = new WriterStreamConsumer(logWriter);
      StreamConsumer err = new WriterStreamConsumer(logWriter);
      return CommandLineUtils.executeCommandLine(cmd, out, err);
    } catch (IOException | CommandLineException e) {
      throw new VerifierException(String.format("Could not execute '%s'", action), e);
    } finally {
      IOUtils.closeQuietly(logWriter);
    }
  }

  private void processOptions(Commandline cmd) {
    for (String commandLineArgument : options.getCommandLineArguments()) {
      cmd.createArg().setValue(commandLineArgument);
    }
  }

  public void assertFilePresent(String fileName) throws VerifierException {
    File expectedFile = new File(baseDirectory, fileName);
    if (!expectedFile.exists()) {
      throw new VerifierException(String.format("Expected file '%s' not found", fileName));
    }
  }

  public void assertLogContainsText(String text) throws VerifierException {
    File logFile = new File(baseDirectory, LOG_FILENAME);
    List<String> lines = loadFile(logFile);
    boolean result = false;
    for (String line : lines) {
      if (line.contains(text)) {
        result = true;
        break;
      }
    }
    if (!result) {
      throw new VerifierException(String.format("Text '%s' not found in log %s", text, logFile.getAbsolutePath()));
    }
  }

  public List<String> loadFile(File file)
      throws VerifierException {
    List<String> lines = new ArrayList<String>();

    BufferedReader reader = null;

    if (file.exists()) {
      try {
        reader = new BufferedReader(new FileReader(file));

        String line = reader.readLine();

        while (line != null) {
          line = line.trim();

          if (!line.startsWith("#") && line.length() != 0) {
            lines.add(line);
          }
          line = reader.readLine();
        }

        reader.close();
      } catch (FileNotFoundException e) {
        throw new VerifierException("Could not read log file", e);
      } catch (IOException e) {
        throw new VerifierException("Could not read log file", e);
      } finally {
        IOUtil.close(reader);
      }
    }

    return lines;
  }

  public static class Maven extends Verifier {

    Maven(String baseDiretory) {
      this(baseDiretory, new Options());
    }

    Maven(String baseDiretory, Options options) {
      super(baseDiretory, options);
    }

    @Override
    protected Commandline createCommandline() {
      Commandline cmd = new Commandline();
      cmd.setExecutable("mvn");
      cmd.setWorkingDirectory(baseDirectory);
      return cmd;
    }

    public static Verifier create(String baseDiretory) {
      return new Maven(baseDiretory);
    }

    public static Verifier create(String baseDiretory, Options options) {
      return new Maven(baseDiretory, options);
    }
  }

  public static class Gradle extends Verifier {

    Gradle(String baseDiretory) {
      this(baseDiretory, new Options());
    }

    Gradle(String baseDiretory, Options options) {
      super(baseDiretory, options);
    }

    @Override
    protected Commandline createCommandline() {
      Commandline cmd = new Commandline();
      cmd.setExecutable("gradle");
      cmd.setWorkingDirectory(baseDirectory);
      return cmd;
    }

    public static Verifier create(String baseDiretory) {
      return new Gradle(baseDiretory);
    }

    public static Verifier create(String baseDiretory, Options options) {
      return new Gradle(baseDiretory, options);
    }
  }
}
