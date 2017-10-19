package io.mathan.maven.latex.features;

import io.mathan.maven.latex.AbstractIntegrationTest;
import io.mathan.maven.latex.internal.Step;
import org.junit.Test;

public class MakeindexTest extends AbstractIntegrationTest {
    @Test
    public void pdf() throws Exception {
        ITVerifier verifier = verifier("features","makeindex");
        verifier
                .verifyExecution(Step.STEP_MAKEINDEX)
                .verifySkipped(Step.STEP_MAKEINDEXNOMENCL);
    }
}
