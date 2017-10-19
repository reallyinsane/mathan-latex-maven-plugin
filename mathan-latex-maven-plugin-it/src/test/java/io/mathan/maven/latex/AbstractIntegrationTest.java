package io.mathan.maven.latex;

import io.mathan.maven.latex.internal.Step;
import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;

import java.io.File;

public abstract class AbstractIntegrationTest {

    protected ITVerifier verifier(String category, String project) throws Exception {
        return verifier(category, project, "mathan:latex");
    }

    protected ITVerifier verifier(String category, String project, String goal) throws Exception {
        return verifier(category, project, goal, "pdf");
    }

    protected ITVerifier verifier(String category, String project, String goal, String extension) throws Exception {
        File dir = ResourceExtractor.simpleExtractResources(getClass(), String.format("/%s/%s", category, project));
        Verifier verifier = new Verifier(dir.getAbsolutePath());
        verifier.executeGoal(goal);
        verifier.assertFilePresent(String.format("target/%s-0.0.2-SNAPSHOT.%s", project, extension));
        return new ITVerifier(verifier);
    }

    public static class ITVerifier  {
        private final Verifier verifier;
        ITVerifier(Verifier verifier) {
            this.verifier = verifier;
        }
        public ITVerifier assertFilePresent(String file) {
            verifier.assertFilePresent(file);
            return this;
        }
        public ITVerifier verifyTextInLog(String text) throws VerificationException {
            verifier.verifyTextInLog(text);
            return this;
        }
        public ITVerifier verifyExecution(Step step) throws VerificationException {
            verifier.verifyTextInLog(String.format("[mathan] execution: %s", step.getId()));
            return this;
        }
        public ITVerifier verifySkipped(Step step) throws VerificationException {
            verifier.verifyTextInLog(String.format("[mathan] execution skipped: %s", step.getId()));
            return this;

        }
    }
}
