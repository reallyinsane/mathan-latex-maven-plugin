/*
 * Copyright 2017 Matthias Hanisch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        verifier.assertFilePresent(String.format("target/%s-0.9.0.%s", project, extension));
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
