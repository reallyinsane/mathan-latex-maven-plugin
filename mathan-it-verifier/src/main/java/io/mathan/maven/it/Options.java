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

import java.util.ArrayList;
import java.util.List;

/**
 * These options can be used with the {@link Verifier}.
 */
public class Options {

  private String workingDirectory;
  private List<String> commandLineArguments = new ArrayList<>();

  /**
   * Returns the command line arguments to append to the command line created by the Verifier.
   *
   * @return List of command line arguments.
   */
  public List<String> getCommandLineArguments() {
    return commandLineArguments;
  }

  public String getWorkingDirectory() {
    return workingDirectory;
  }

  public void setWorkingDirectory(String workingDirectory) {
    this.workingDirectory = workingDirectory;
  }
}
