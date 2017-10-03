package io.mathan.maven.latex;

import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

public class DependencyTest {

    @Test
    public void includeTex() throws Exception {
        File dir = ResourceExtractor.simpleExtractResources(getClass(), "/tex/simple");
        Verifier verifier = new Verifier(dir.getAbsolutePath());
        verifier.executeGoal("install");
        dir = ResourceExtractor.simpleExtractResources(getClass(), "/dependency");
        verifier = new Verifier(dir.getAbsolutePath());
        verifier.executeGoal("mathan:latex");

    }
}
