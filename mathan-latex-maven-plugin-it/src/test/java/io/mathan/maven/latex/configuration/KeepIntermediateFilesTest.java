package io.mathan.maven.latex.configuration;

import io.mathan.maven.latex.AbstractIntegrationTest;
import org.junit.Test;

public class KeepIntermediateFilesTest extends AbstractIntegrationTest {
    @Test
    public void keepintermediatefiles() throws Exception {
        ITVerifier verifier = verifier("configuration", "keepintermediatefiles");
        verifier.assertFilePresent("target/latex/mathan-latex-mojo.log");
    }
}
