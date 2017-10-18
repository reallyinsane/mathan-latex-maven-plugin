package io.mathan.maven.latex.dependencies;

import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;
import org.junit.Test;

import java.io.File;

public class DependencyTest {

    @Test
    public void includeTex() throws Exception {
        File dir = ResourceExtractor.simpleExtractResources(getClass(), "/dependencies/dependency");
        Verifier verifier = new Verifier(dir.getAbsolutePath());
        verifier.executeGoal("install");
        dir = ResourceExtractor.simpleExtractResources(getClass(), "/dependencies/main");
        verifier = new Verifier(dir.getAbsolutePath());
        verifier.executeGoal("mathan:latex");

    }
}
