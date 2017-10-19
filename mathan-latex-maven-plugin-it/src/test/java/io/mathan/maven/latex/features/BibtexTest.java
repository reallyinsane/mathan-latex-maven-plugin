package io.mathan.maven.latex.features;

import io.mathan.maven.latex.AbstractIntegrationTest;
import io.mathan.maven.latex.internal.Step;
import org.junit.Test;

public class BibtexTest extends AbstractIntegrationTest {
    @Test
    public void pdf() throws Exception {
        ITVerifier verifier = verifier("features", "bibtex");
        verifier.verifyExecution(Step.STEP_BIBTEX);
    }
}
