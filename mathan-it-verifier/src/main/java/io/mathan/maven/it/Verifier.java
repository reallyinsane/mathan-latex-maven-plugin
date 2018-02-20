/*
 * Copyright 2018 Matthias Hanisch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.mathan.maven.it;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.zeroturnaround.exec.ProcessExecutor;

/**
 * Replacement for maven-verifier supporting execution of any command. The Verifier executes a command and can then check for presence of files or checking the log content.
 */
public abstract class Verifier {

  private final String LOG_FILENAME = "verifier.log";

  private final String baseDirectory;
  private final Options options;

  /**
   * Creates a Verifier for the execution in the given working directory using the provided {@link Options options}.
   *
   * @param baseDirectory The working directory.
   * @param options The options to use.
   */
  Verifier(String baseDirectory, Options options) {
    this.baseDirectory = baseDirectory;
    this.options = options;
  }

  /**
   * Returns the working directory of this Verifier.
   *
   * @return The working directory.
   */
  protected String getBaseDirectory() {
    return this.baseDirectory;
  }

  /**
   * Creates the command line to execute.
   *
   * @return The command line.
   */
  protected abstract String getExecutable();

  /**
   * Executes the given action with this Verifier.
   *
   * @param action The action to execute (e.g. a Maven goal or Gradle task).
   * @return The return code of the execution.
   * @throws VerifierException If the execution failed.
   */
  public int execute(String action) throws VerifierException {
    List<String> commands = new ArrayList<>();
    commands.add(getExecutable());
    commands.addAll(options.getCommandLineArguments());
    commands.add(action);
    ProcessExecutor executor = new ProcessExecutor(commands);
    if (options.getWorkingDirectory() != null) {
      executor.directory(new File(options.getWorkingDirectory()));
    }
    OutputStream logStream = null;
    try {
      logStream = new FileOutputStream(new File(baseDirectory, LOG_FILENAME));
      executor.redirectOutput(logStream);
      executor.redirectError(logStream);
      return executor.execute().getExitValue();
    } catch (IOException | InterruptedException | TimeoutException e) {
      throw new VerifierException(String.format("Could not execute '%s'", action), e);
    } finally {
      IOUtils.closeQuietly(logStream);
    }
  }

  /**
   * Verifies that the given file exists.
   *
   * @param fileName The name of the file to check.
   * @throws VerifierException If the file does not exist.
   */
  public void assertFilePresent(String fileName) throws VerifierException {
    File expectedFile = new File(baseDirectory, fileName);
    if (!expectedFile.exists()) {
      throw new VerifierException(String.format("Expected file '%s' not found", fileName));
    }
  }

  /**
   * Verifies that the log contains the given text.
   *
   * @param text The text to check.
   * @throws VerifierException If the log does not contain the text.
   */
  public void assertLogContainsText(String text) throws VerifierException {
    File logFile = new File(baseDirectory, LOG_FILENAME);
    List<String> lines = null;
    try {
      lines = FileUtils.readLines(logFile, "UTF-8");
    } catch (IOException e) {
      throw new VerifierException(String.format("Could not read log file %s", logFile.getAbsolutePath()), e);
    }
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

  /**
   * Verifier executing the maven command.
   */
  public static class Maven extends Verifier {

    private String executable = System.getProperty("os.name", "generic").toLowerCase().contains("win") ? "mvn.cmd" : "mvn";

    Maven(String baseDiretory) {
      this(baseDiretory, new Options());
    }

    Maven(String baseDiretory, Options options) {
      super(baseDiretory, options);
    }

    public void setExecutable(String executable) {
      this.executable = executable;
    }

    @Override
    public String getExecutable() {
      return executable;
    }

    public static Verifier create(String baseDiretory) {
      return new Maven(baseDiretory);
    }

    public static Verifier create(String baseDiretory, Options options) {
      return new Maven(baseDiretory, options);
    }
  }

  /**
   * Verifier executing the gradle command.
   */
  public static class Gradle extends Verifier {

    private String executable = System.getProperty("os.name", "generic").toLowerCase().contains("win") ? "gradle.bat" : "gradle";

    Gradle(String baseDiretory) {
      this(baseDiretory, new Options());
    }

    Gradle(String baseDiretory, Options options) {
      super(baseDiretory, options);
    }

    @Override
    public String getExecutable() {
      return executable;
    }

    public void setExecutable(String executable) {
      this.executable = executable;
    }

    public static Verifier create(String baseDiretory) {
      return new Gradle(baseDiretory);
    }

    public static Verifier create(String baseDiretory, Options options) {
      return new Gradle(baseDiretory, options);
    }
  }
}
