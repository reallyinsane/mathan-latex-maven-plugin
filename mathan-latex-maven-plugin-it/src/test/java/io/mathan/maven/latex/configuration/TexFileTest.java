package io.mathan.maven.latex.configuration;

import io.mathan.maven.latex.AbstractIntegrationTest;
import io.mathan.maven.latex.internal.Step;
import org.junit.Test;

public class TexFileTest extends AbstractIntegrationTest {
    @Test
    public void textfileExists() throws Exception {
        ITVerifier verifier = verifier("configuration", "texfile");
        verifier
                .verifySkipped(Step.STEP_MAKEINDEX)
                .verifySkipped(Step.STEP_MAKEINDEXNOMENCL);
    }
}
