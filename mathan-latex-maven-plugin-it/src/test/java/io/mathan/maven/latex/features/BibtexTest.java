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
package io.mathan.maven.latex.features;

import io.mathan.latex.core.Step;
import io.mathan.maven.latex.AbstractIntegrationTest;
import org.apache.maven.it.Verifier;
import org.junit.Test;

public class BibtexTest extends AbstractIntegrationTest {

  @Test
  public void pdf() throws Exception {
    Verifier verifier = verifier("features", "bibtex");
    assertStepExecuted(verifier, Step.STEP_BIBTEX);
  }
}
