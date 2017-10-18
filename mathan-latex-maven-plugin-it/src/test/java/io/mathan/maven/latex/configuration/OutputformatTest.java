package io.mathan.maven.latex.configuration;

import io.mathan.maven.latex.internal.Constants;
import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;
import org.junit.Test;

import java.io.File;

/**
 * Simple test generating an output file for each supported file without bibtex/makeindex.
 */
public class OutputformatTest {

    @Test
    public void pdf() throws Exception {
        testSuccess(Constants.FORMAT_PDF);
    }

    @Test
    public void ps() throws Exception {
        testSuccess(Constants.FORMAT_PS);
    }

    @Test
    public void dvi() throws Exception {
        testSuccess(Constants.FORMAT_DVI);
    }

    @Test
    public void invalidOutputFormat() throws Exception {
        File dir = ResourceExtractor.simpleExtractResources(getClass(), "/configuration/outputformat/invalid");
        Verifier verifier = new Verifier(dir.getAbsolutePath());
        try {
            verifier.executeGoal("package");
        } catch (VerificationException e) {
            verifier.verifyTextInLog("Invalid outputFormat");
        }
    }

    private void testSuccess(String outputFormat) throws Exception {
        File dir = ResourceExtractor.simpleExtractResources(getClass(), "/configuration/outputformat/" + outputFormat);
        Verifier verifier = new Verifier(dir.getAbsolutePath());
        verifier.executeGoal("mathan:latex");
        verifier.assertFilePresent("target/simple_"+outputFormat+"-0.0.2-SNAPSHOT." + outputFormat);
        // as there is no bibtex, makeindex file these steps will be skipped
        verifier.verifyTextInLog("[mathan] execution skipped: bibtex");
        verifier.verifyTextInLog("[mathan] execution skipped: makeindex");
        verifier.verifyTextInLog("[mathan] execution skipped: makeindexnomencl");
    }

}
