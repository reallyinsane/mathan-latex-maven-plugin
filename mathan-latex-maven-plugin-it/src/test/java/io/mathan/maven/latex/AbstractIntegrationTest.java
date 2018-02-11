/*
 * Copyright 2017 Matthias Hanisch
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
package io.mathan.maven.latex;

import io.mathan.latex.core.Step;
import java.io.File;
import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;

public abstract class AbstractIntegrationTest {

  private static final String VERSION = "0.9.1-SNAPSHOT";

  protected Verifier verifier(String category, String project) throws Exception {
    return verifier(category, project, "mathan:latex");
  }

  protected Verifier verifier(String category, String project, String goal) throws Exception {
    return verifier(category, project, goal, "pdf");
  }

  protected Verifier verifier(String category, String project, String goal, String extension) throws Exception {
    return verifier(category, project, goal, extension, null);
  }

  protected Verifier verifier(String category, String project, String goal, String extension, String classifier) throws Exception {
    File dir = ResourceExtractor.simpleExtractResources(getClass(), String.format("/%s/%s", category, project));
    Verifier verifier = new Verifier(dir.getAbsolutePath());
    verifier.executeGoal(goal);
    if (classifier == null) {
      verifier.assertFilePresent(String.format("target/%s-%s.%s", project, VERSION, extension));
    } else {
      verifier.assertFilePresent(String.format("target/%s-%s-%s.%s", project, VERSION, classifier, extension));
    }
    return verifier;
  }

  protected final void assertBuild(String category, String project) throws Exception {
    verifier(category, project);
  }

  protected final void assertBuild(String category, String project, String goal, String extension, String classifier) throws Exception {
    verifier(category, project, goal, extension, classifier);
  }

  protected final void assertBuild(String category, String project, String goal, String extension) throws Exception {
    assertBuild(category, project, goal, extension, null);
  }

  protected final void assertFilePresent(Verifier verifier, String file) {
    verifier.assertFilePresent(file);
  }

  protected final void verifyTextInLog(Verifier verifier, String text) throws VerificationException {
    verifier.verifyTextInLog(text);
  }

  protected final void assertStepExecuted(Verifier verifier, Step step) throws VerificationException {
    verifier.verifyTextInLog(String.format("[mathan] execution: %s", step.getId()));
  }

  protected final void assertStepSkipped(Verifier verifier, Step step) throws VerificationException {
    verifier.verifyTextInLog(String.format("[mathan] execution skipped: %s", step.getId()));
  }
}
