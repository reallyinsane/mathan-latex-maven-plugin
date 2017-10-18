package io.mathan.maven.latex;

import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;
import org.junit.Test;

import java.io.File;

public class BibtexTest {
    @Test
    public void pdf() throws Exception {
        File dir = ResourceExtractor.simpleExtractResources(getClass(), "/features/bibtex");
        Verifier verifier = new Verifier(dir.getAbsolutePath());
        verifier.executeGoal("mathan:latex");
        verifier.assertFilePresent("target/bibtex-0.0.2-SNAPSHOT.pdf");
        verifier.verifyTextInLog("[mathan] execution: bibtex");
        verifier.verifyTextInLog("[mathan] execution skipped: makeindex");
        verifier.verifyTextInLog("[mathan] execution skipped: makeindexnomencl");
    }
}
