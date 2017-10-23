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
package io.mathan.maven.latex.configuration;

import io.mathan.maven.latex.AbstractIntegrationTest;
import io.mathan.maven.latex.internal.Step;
import org.apache.maven.it.Verifier;
import org.junit.Test;

public class MakeindexstylefileTest extends AbstractIntegrationTest {
    @Test
    public void stylefileExists() throws Exception {
        Verifier verifier = verifier("configuration","makeindexstylefile");
        verifyExecution(verifier, Step.STEP_MAKEINDEX);
    }
}
