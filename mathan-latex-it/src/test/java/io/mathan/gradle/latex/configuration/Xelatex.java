package io.mathan.gradle.latex.configuration;

import io.mathan.gradle.latex.AbstractIntegrationTest;
import io.mathan.latex.core.Step;
import io.mathan.maven.it.Verifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class Xelatex extends AbstractIntegrationTest {

  public Xelatex(Build build) {
    super(build);
  }

  @Test
  public void xelatex() throws Exception {
    Verifier verifier = verifier("configuration", "xelatex");
    assertStepExecuted(verifier, Step.STEP_XELATEX);
  }

}
