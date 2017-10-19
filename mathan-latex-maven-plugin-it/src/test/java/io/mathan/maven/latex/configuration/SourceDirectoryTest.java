package io.mathan.maven.latex.configuration;

import io.mathan.maven.latex.AbstractIntegrationTest;
import org.junit.Test;

public class SourceDirectoryTest extends AbstractIntegrationTest{
    @Test
    public void sourceDirectoryExists() throws Exception {
        verifier("configuration", "sourcedirectory");
    }
}
