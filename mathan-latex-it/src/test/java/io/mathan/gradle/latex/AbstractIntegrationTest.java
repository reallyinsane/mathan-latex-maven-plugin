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
package io.mathan.gradle.latex;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.mathan.latex.core.Step;
import io.mathan.maven.it.Options;
import io.mathan.maven.it.Verifier;
import io.mathan.maven.it.VerifierException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.runners.Parameterized.Parameters;

public abstract class AbstractIntegrationTest {

  private static final boolean KEEP_TEMP_DIR = Boolean.getBoolean("mathan-keep-temp-dir");

  protected Build build;

  private List<File> temporaryDirectories = new ArrayList<>();


  protected AbstractIntegrationTest(Build build) {
    this.build = build;
  }

  @Parameters(name = "build={0}")
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[]{Build.Maven}, new Object[]{Build.Gradle});
  }

  private File createTemporaryDirectory() {
    String tempDirPath = System.getProperty("maven.test.tmpdir", System.getProperty("java.io.tmpdir"));
    File tempDir = new File(tempDirPath);
    File temporaryDirectory = new File(tempDir, UUID.randomUUID().toString());
    temporaryDirectories.add(temporaryDirectory);
    return temporaryDirectory;
  }

  @After
  public void cleanUp() throws Exception {
    if (!KEEP_TEMP_DIR) {
      temporaryDirectories.forEach(dir -> {
        try {
          FileUtils.deleteDirectory(dir);
        } catch (IOException e) {
        }
      });
    }
  }


  protected enum Build {
    Maven("build.gradle", "settings.gradle"), Gradle("pom.xml");
    private String[] filesToIgnore;

    Build(String... filesToIgnore) {
      this.filesToIgnore = filesToIgnore;
    }

    boolean ignore(String name) {
      for (String fileToIgnore : filesToIgnore) {
        if (name.endsWith(fileToIgnore)) {
          return true;
        }
      }
      return false;
    }
  }

  private static final String VERSION = "0.9.1-SNAPSHOT";

  protected String latexGoal() {
    switch (build) {
      case Maven:
        return "mathan:latex";
      case Gradle:
        return "latex";
      default:
        Assert.fail(String.format("Unknown build: %s", build));
        return null;
    }
  }

  protected Verifier verifier(String category, String project) throws Exception {
    return verifier(category, project, latexGoal());
  }

  protected Verifier verifier(String category, String project, String goal) throws Exception {
    return verifier(category, project, goal, "pdf");
  }

  protected Verifier verifier(String category, String project, String goal, String extension) throws Exception {
    return verifier(category, project, goal, extension, null);
  }

  protected Verifier verifier(String category, String project, String goal, String extension, String classifier) throws Exception {
    File temporaryDirectory = createTemporaryDirectory();
    extractResourcesToTempDir(String.format("%s/%s", category, project), temporaryDirectory);
    Verifier verifier = createVerifier(temporaryDirectory.getAbsolutePath());
    verifier.execute(goal);
    if (classifier == null) {
      verifier.assertFilePresent(String.format("target/%s-%s.%s", project, VERSION, extension));
    } else {
      verifier.assertFilePresent(String.format("target/%s-%s-%s.%s", project, VERSION, classifier, extension));
    }
    return verifier;
  }

  protected void publish(String category, String project) throws Exception {
    File temporaryDirectory = createTemporaryDirectory();
    extractResourcesToTempDir(String.format("%s/%s", category, project), temporaryDirectory);
    Verifier verifier = createVerifier(temporaryDirectory.getAbsolutePath());
    switch (build) {
      case Gradle:
        verifier.execute("publishToMavenLocal");
        break;
      case Maven:
        verifier.execute("install");
        break;
    }
  }

  private Verifier createVerifier(String baseDirectory) {
    switch (build) {
      case Maven:
        return Verifier.Maven.create(baseDirectory);
      case Gradle:
        Options options = new Options();
        // enable log output for level INFO
        options.getCommandLineArguments().add("-i");
        return Verifier.Gradle.create(baseDirectory, options);
      default:
        Assert.fail(String.format("Unknown build: %s", build));
        return null;
    }
  }

  protected final void assertFilePresent(Verifier verifier, String file) throws VerifierException {
    verifier.assertFilePresent(file);
  }

  protected final void verifyTextInLog(Verifier verifier, String text) throws VerifierException {
    verifier.assertLogContainsText(text);
  }

  protected final void assertStepExecuted(Verifier verifier, Step step) throws VerifierException {
    verifier.assertLogContainsText(String.format("[mathan] execution: %s", step.getId()));
  }

  protected final void assertStepSkipped(Verifier verifier, Step step) throws VerifierException {
    verifier.assertLogContainsText(String.format("[mathan] execution skipped: %s", step.getId()));
  }

  private final void extractResourcesToTempDir(String path, File temporaryDirectory) {
    FastClasspathScanner scanner = new FastClasspathScanner();
    scanner.matchFilenamePattern(path + "/.*", (classpathElt, relativePath, inputStream, lengthBytes) -> {
      if (build.ignore(relativePath)) {
        return;
      }
      String filePath = relativePath.substring((path + "/").length());
      File file = new File(temporaryDirectory, filePath);
      File parent = file.getParentFile();
      if (!parent.exists()) {
        parent.mkdirs();
      }
      FileOutputStream out = new FileOutputStream(file);
      IOUtils.copy(inputStream, out);
      out.close();
      inputStream.close();
    }).scan();
  }
}
