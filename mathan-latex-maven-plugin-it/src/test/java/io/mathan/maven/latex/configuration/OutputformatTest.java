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
package io.mathan.maven.latex.configuration;

import io.mathan.latex.core.Constants;
import io.mathan.latex.core.Step;
import io.mathan.maven.latex.AbstractIntegrationTest;
import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.junit.Test;

/**
 * Simple test generating an output file for each supported file without bibtex/makeindex.
 */
public class OutputformatTest extends AbstractIntegrationTest {

  @Test
  public void pdf() throws Exception {
    assertSuccess(Constants.FORMAT_PDF, Step.STEP_PDFLATEX);
  }

  @Test
  public void ps() throws Exception {
    assertSuccess(Constants.FORMAT_PS, Step.STEP_LATEX, Step.STEP_DVIPS);
  }

  @Test
  public void dvi() throws Exception {
    assertSuccess(Constants.FORMAT_DVI, Step.STEP_LATEX);
  }


  @Test(expected = VerificationException.class)
  public void invalid() throws Exception {
    verifier("configuration/outputformat", "invalid");
  }

  private void assertSuccess(String outputFormat, Step... steps) throws Exception {
    Verifier verifier = verifier("configuration/outputformat", outputFormat, "mathan:latex", outputFormat);
    for (Step step : steps) {
      assertStepExecuted(verifier, step);
    }
  }

}
