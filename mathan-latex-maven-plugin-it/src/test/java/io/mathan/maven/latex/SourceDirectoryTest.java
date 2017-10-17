package io.mathan.maven.latex;

import io.mathan.maven.latex.internal.Constants;
import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;
import org.junit.Test;

import java.io.File;

public class SourceDirectoryTest {
    @Test
    public void pdf() throws Exception {
        File dir = ResourceExtractor.simpleExtractResources(getClass(), "/source_directory");
        Verifier verifier = new Verifier(dir.getAbsolutePath());
        verifier.executeGoal("mathan:latex");
        verifier.assertFilePresent("target/source_directory-0.0.2-SNAPSHOT.pdf");
        // as there is no bibtex, makeindex file these steps will be skipped
        verifier.verifyTextInLog("[mathan] execution skipped: bibtex");
        verifier.verifyTextInLog("[mathan] execution skipped: makeindex");
        verifier.verifyTextInLog("[mathan] execution skipped: makeindexnomencl");
    }
}
