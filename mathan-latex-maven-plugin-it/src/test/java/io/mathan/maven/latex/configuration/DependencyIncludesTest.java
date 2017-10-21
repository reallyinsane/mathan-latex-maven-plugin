package io.mathan.maven.latex.configuration;

import io.mathan.maven.latex.AbstractIntegrationTest;
import org.junit.Test;

public class DependencyIncludesTest extends AbstractIntegrationTest {
    @Test
    public void bibtex() throws Exception {
        verifier("dependencies", "dependency", "install", "jar");
        verifier("configuration", "dependencyincludes");
    }
}
