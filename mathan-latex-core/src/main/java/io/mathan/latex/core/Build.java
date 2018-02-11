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

package io.mathan.latex.core;

import java.io.File;
import org.zeroturnaround.exec.stream.LogOutputStream;

/**
 * Interface for the LaTeX execution with a build system. (e.g. Maven)
 */
public interface Build {

  /**
   * Returns the log of the build system
   *
   * @return The log of the build system.
   */
  BuildLog getLog();

  /**
   * Returns the basedir of the build execution.
   *
   * @return The basedir.
   */
  File getBasedir();

  /**
   * Returns the arrtifactId of the project to build.
   *
   * @return The artifactId.
   */
  String getArtifactId();

  /**
   * Returns the version of the project to build.
   *
   * @return The version.
   */
  String getVersion();

  /**
   * Sets the given artifact as artifact of the build.
   *
   * @param artifact The artifact to set.
   */
  void setArtifact(File artifact);

  /**
   * Resolves required dependencies for the project.
   *
   * @param workingDirectory The working directory.
   */
  void resolveDependencies(File workingDirectory) throws LatexExecutionException;

  /**
   * Returns a LogOutputStream for debug output to use for executions by the build system.
   */
  LogOutputStream getRedirectOutput(String prefix);

  /**
   * Returns a LogOutputStream for error output to use for executions by the build system.
   */
  LogOutputStream getRedirectError(String prefix);
}
