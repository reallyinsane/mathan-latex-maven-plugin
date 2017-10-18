package io.mathan.maven.latex.features;

import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;
import org.junit.Test;

import java.io.File;

public class MakeindexTest {
    @Test
    public void pdf() throws Exception {
        File dir = ResourceExtractor.simpleExtractResources(getClass(), "/features/makeindex");
        Verifier verifier = new Verifier(dir.getAbsolutePath());
        verifier.executeGoal("mathan:latex");
        verifier.assertFilePresent("target/makeindex-0.0.2-SNAPSHOT.pdf");
        verifier.verifyTextInLog("[mathan] execution skipped: bibtex");
        verifier.verifyTextInLog("[mathan] execution: makeindex");
        verifier.verifyTextInLog("[mathan] execution skipped: makeindexnomencl");
    }
}
