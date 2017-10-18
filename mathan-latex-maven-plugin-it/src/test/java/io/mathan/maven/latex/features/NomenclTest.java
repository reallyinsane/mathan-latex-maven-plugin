package io.mathan.maven.latex.features;

import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;
import org.junit.Test;

import java.io.File;

public class NomenclTest {
    @Test
    public void pdf() throws Exception {
        File dir = ResourceExtractor.simpleExtractResources(getClass(), "/features/nomencl");
        Verifier verifier = new Verifier(dir.getAbsolutePath());
        verifier.executeGoal("mathan:latex");
        verifier.assertFilePresent("target/nomencl-0.0.2-SNAPSHOT.pdf");
        verifier.verifyTextInLog("[mathan] execution skipped: bibtex");
        verifier.verifyTextInLog("[mathan] execution: makeindex");
        verifier.verifyTextInLog("[mathan] execution: makeindexnomencl");
    }
}
