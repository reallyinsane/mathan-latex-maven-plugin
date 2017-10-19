package io.mathan.maven.latex.dependencies;

import io.mathan.maven.latex.AbstractIntegrationTest;
import org.junit.Test;

public class DependencyTest extends AbstractIntegrationTest {

    @Test
    public void includeTex() throws Exception {
        verifier("dependencies", "dependency", "install", "jar");
        verifier("dependencies", "main");
    }
}
