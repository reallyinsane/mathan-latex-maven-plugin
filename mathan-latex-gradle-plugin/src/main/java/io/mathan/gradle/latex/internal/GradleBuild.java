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
