package io.mathan.gradle.latex;

import io.mathan.latex.core.MathanLatexConfiguration;
import java.util.HashMap;
import java.util.Map;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class MathanLatexPlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    MathanLatexConfiguration extension = project.getExtensions().create("latex", MathanLatexConfiguration.class);
    Map<String, Object> map = new HashMap<>();
    map.put("type", MathanLatexTask.class);
    MathanLatexTask task = (MathanLatexTask) project.task(map, "latex");
    task.getOutputs().upToDateWhen(t -> false);
    task.setConfiguration(extension);

  }
}
