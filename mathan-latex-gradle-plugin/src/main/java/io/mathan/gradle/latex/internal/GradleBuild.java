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

package io.mathan.gradle.latex.internal;

import io.mathan.latex.core.Build;
import io.mathan.latex.core.BuildLog;
import io.mathan.latex.core.LatexExecutionException;
import java.io.File;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.zeroturnaround.exec.stream.LogOutputStream;

public class GradleBuild implements Build {

  private Project project;
  private DefaultTask task;

  public GradleBuild(Project project, DefaultTask task) {

    this.project = project;
    this.task = task;
  }

  @Override
  public BuildLog getLog() {
    return new GradleBuildLog(task.getLogger());
  }

  @Override
  public File getBasedir() {
    return project.getProjectDir();
  }

  @Override
  public String getArtifactId() {
    return project.getName();
  }

  @Override
  public String getVersion() {
    return project.getVersion().toString();
  }

  @Override
  public void setArtifact(File artifact) {
  }

  @Override
  public void resolveDependencies(File workingDirectory) throws LatexExecutionException {
  }

  @Override
  public LogOutputStream getRedirectOutput(String prefix) {
    return GradleLogOutputStream.toDebug(task.getLogger(), prefix);
  }

  @Override
  public LogOutputStream getRedirectError(String prefix) {
    return GradleLogOutputStream.toError(task.getLogger(), prefix);
  }
}
