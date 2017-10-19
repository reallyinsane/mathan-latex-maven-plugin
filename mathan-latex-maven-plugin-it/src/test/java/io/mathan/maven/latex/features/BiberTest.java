package io.mathan.maven.latex.features;

import io.mathan.maven.latex.AbstractIntegrationTest;
import io.mathan.maven.latex.internal.Step;
import org.junit.Test;

public class BiberTest extends AbstractIntegrationTest{
    @Test
    public void pdf() throws Exception {
        ITVerifier verifier = verifier("features", "biber");
        verifier.verifyExecution(Step.STEP_BIBER);
    }
}
