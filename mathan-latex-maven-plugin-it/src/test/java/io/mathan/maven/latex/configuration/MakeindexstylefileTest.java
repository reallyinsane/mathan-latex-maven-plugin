package io.mathan.maven.latex.configuration;

import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;
import org.junit.Test;

import java.io.File;

public class MakeindexstylefileTest {
    @Test
    public void pdf() throws Exception {
        File dir = ResourceExtractor.simpleExtractResources(getClass(), "/configuration/makeindexstylefile");
        Verifier verifier = new Verifier(dir.getAbsolutePath());
        verifier.executeGoal("mathan:latex");
        verifier.assertFilePresent("target/makeindexstylefile-0.0.2-SNAPSHOT.pdf");
        verifier.verifyTextInLog("[mathan] execution skipped: bibtex");
        verifier.verifyTextInLog("[mathan] execution: makeindex");
        verifier.verifyTextInLog("[mathan] execution skipped: makeindexnomencl");
    }
}
