package io.mathan.maven.latex.configuration;

import io.mathan.maven.latex.AbstractIntegrationTest;
import io.mathan.maven.latex.internal.Constants;
import io.mathan.maven.latex.internal.Step;
import org.apache.maven.it.VerificationException;
import org.junit.Test;

/**
 * Simple test generating an output file for each supported file without bibtex/makeindex.
 */
public class OutputformatTest extends AbstractIntegrationTest{

    @Test
    public void pdf() throws Exception {
        testSuccess(Constants.FORMAT_PDF, Step.STEP_PDFLATEX);
    }

    @Test
    public void ps() throws Exception {
        testSuccess(Constants.FORMAT_PS, Step.STEP_LATEX, Step.STEP_DVIPS);
    }

    @Test
    public void dvi() throws Exception {
        testSuccess(Constants.FORMAT_DVI, Step.STEP_LATEX);
    }


    @Test(expected = VerificationException.class)
    public void invalid() throws Exception {
        verifier("configuration/outputformat", "invalid");
    }

    private void testSuccess(String outputFormat, Step... steps) throws Exception {
        ITVerifier verifier = verifier("configuration/outputformat", outputFormat, "mathan:latex", outputFormat);
        for(Step step:steps) {
            verifier.verifyExecution(step);
        }
    }

}
