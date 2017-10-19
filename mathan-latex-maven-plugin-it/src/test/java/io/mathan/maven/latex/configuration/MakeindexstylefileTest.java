package io.mathan.maven.latex.configuration;

import io.mathan.maven.latex.AbstractIntegrationTest;
import io.mathan.maven.latex.internal.Step;
import org.junit.Test;

public class MakeindexstylefileTest extends AbstractIntegrationTest {
    @Test
    public void stylefileExists() throws Exception {
        ITVerifier verifier = verifier("configuration","makeindexstylefile");
        verifier.verifyExecution(Step.STEP_MAKEINDEX);
    }
}
