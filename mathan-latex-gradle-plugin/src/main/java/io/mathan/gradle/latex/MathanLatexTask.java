package io.mathan.gradle.latex;

import io.mathan.gradle.latex.internal.GradleBuild;
import io.mathan.latex.core.LatexExecutionException;
import io.mathan.latex.core.MathanLatexConfiguration;
import io.mathan.latex.core.MathanLatexRunner;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class MathanLatexTask extends DefaultTask {

  private MathanLatexConfiguration configuration;

  public void setConfiguration(MathanLatexConfiguration configuration) {
    this.configuration = configuration;
  }

  @TaskAction
  public void latex() {
    configuration.setKeepIntermediateFiles(true);

    MathanLatexRunner runner = new MathanLatexRunner(configuration, new GradleBuild(this.getProject(), this));
    try {
      runner.execute();
    } catch (LatexExecutionException e) {
      e.printStackTrace();
    }
    System.out.println("heyou");
  }

}
