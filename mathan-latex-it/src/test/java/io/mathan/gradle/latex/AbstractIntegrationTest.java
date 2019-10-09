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

import io.github.classgraph.ClassGraph;
import io.github.classgraph.Resource;
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

  private static final String VERSION = "1.0.3";
  /**
   * System property to prevent the temporary directories to be removed. (For debugging purpose).
   */
  private static final boolean KEEP_TEMP_DIR = Boolean.getBoolean("mathan-keep-temp-dir");

  /**
   * The build to use for the test. Can be {@link Build#Gradle Gradle} or {@link Build#Maven Maven}.
   */
  protected Build build;

  /**
   * During the test multiple instances of {@link Verifier} can be used. Therefor multiple temporary directories are created. They're all stored to be able to clean them up.
   */
  private List<File> temporaryDirectories = new ArrayList<>();


  protected AbstractIntegrationTest(Build build) {
    this.build = build;
  }

  /**
   * All sub tests should be annotated with {@link org.junit.runner.RunWith} and {@link org.junit.runners.Parameterized} so that they are executed with a Gradle and a Maven build.
   */
  @Parameters(name = "build={0}")
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[]{Build.Maven}, new Object[]{Build.Gradle});
  }

  /**
   * Creates a temporary directory with a {@link UUID} as name and returns it.
   *
   * @return The created temporary directory.
   */
  private File createTemporaryDirectory() {
    String tempDirPath = System.getProperty("maven.test.tmpdir", System.getProperty("java.io.tmpdir"));
    File tempDir = new File(tempDirPath);
    File temporaryDirectory = new File(tempDir, UUID.randomUUID().toString());
    temporaryDirectory.mkdirs();
    temporaryDirectories.add(temporaryDirectory);
    return temporaryDirectory;
  }

  @After
  public void cleanUp() throws Exception {
    if (!KEEP_TEMP_DIR) {
      temporaryDirectories.forEach(dir -> {
        try {
          FileUtils.deleteDirectory(dir);
        } catch (IOException ignored) {
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

    /**
     * Returns true if the given file should be ignored when moving the test resources to the temporary directory. This is used to not copy maven resources for a gradle build and vice versa.
     *
     * @return <code>True</code> if the file should be ignored.
     */
    boolean ignore(String name) {
      for (String fileToIgnore : filesToIgnore) {
        if (name.endsWith(fileToIgnore)) {
          return true;
        }
      }
      return false;
    }
  }

  /**
   * Returns the goal/task to execute for the test. This is the goal <i>mathan:latex</i> for the maven build and the task <i>latex</i> for the gradle build.
   */
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

  /**
   * Publishs the artifact of the given project into the local repository.
   *
   * @param category The category of the test. (First level of directory structure)
   * @param project The project to publish. (Second level of directory structure)
   */
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
    Options options = new Options();
    options.setWorkingDirectory(baseDirectory);
    switch (build) {
      case Maven:
        return Verifier.Maven.create(baseDirectory, options);
      case Gradle:
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

  /**
   * Extracts all resources below the given path from the classpath into a temporary directory.
   *
   * @param path The path specifying which resources to extract from the classpath.
   * @param temporaryDirectory The temporary directory to extract the resources to.
   */
  private void extractResourcesToTempDir(String path, File temporaryDirectory) {
    ClassGraph scanner = new ClassGraph();
    scanner.whitelistPaths(path);
    scanner.scan().getAllResources().forEachInputStream(((resource, inputStream) -> {
      String filePath = resource.getPath().substring((path + "/").length());
      File file = new File(temporaryDirectory, filePath);
      File parent = file.getParentFile();
      if (!parent.exists()) {
        parent.mkdirs();
      }
      try {
        FileOutputStream out = new FileOutputStream(file);
        IOUtils.copy(inputStream, out);
        out.close();
        inputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }

    }));
    /*
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
     */
  }

  private void extract(String path, Resource consumer) {

  }
}
