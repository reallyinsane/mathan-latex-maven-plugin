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
