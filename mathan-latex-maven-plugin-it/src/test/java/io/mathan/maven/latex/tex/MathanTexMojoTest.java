package io.mathan.maven.latex.tex;

import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;
import org.junit.Test;

import java.io.File;

public class MathanTexMojoTest {

    @Test
    public void simple() throws Exception {
            File dir = ResourceExtractor.simpleExtractResources(getClass(), "/tex/simple");
            Verifier verifier = new Verifier(dir.getAbsolutePath());
            verifier.executeGoal("mathan:tex");
            verifier.assertFilePresent("target/tex-simple-0.0.1-SNAPSHOT.zip");
        }

}
