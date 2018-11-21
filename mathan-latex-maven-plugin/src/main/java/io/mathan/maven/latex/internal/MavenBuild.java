/*
 * Copyright 2018 Matthias Hanisch
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

package io.mathan.maven.latex.internal;

import io.mathan.latex.core.Build;
import io.mathan.latex.core.BuildLog;
import io.mathan.latex.core.LatexExecutionException;
import io.mathan.latex.core.Utils;
import io.mathan.maven.latex.MathanLatexMojo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.LocalArtifactRequest;
import org.eclipse.aether.repository.LocalArtifactResult;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.zeroturnaround.exec.stream.LogOutputStream;

public class MavenBuild implements Build {

  private final MathanLatexMojo mojo;

  public MavenBuild(MathanLatexMojo mojo) {
    this.mojo = mojo;
  }

  @Override
  public BuildLog getLog() {
    return new MavenBuildLog(getMojo().getLog());
  }

  @Override
  public File getBasedir() {
    return getProject().getBasedir();
  }

  @Override
  public String getArtifactId() {
    return getProject().getArtifactId();
  }

  @Override
  public String getVersion() {
    return getProject().getVersion();
  }

  @Override
  public void setArtifact(File artifact) {
    getProject().getArtifact().setFile(artifact);

  }

  @Override
  public void resolveDependencies(File workingDirectory) throws LatexExecutionException {
    List<Dependency> dependencies = getProject().getDependencies();
    for (Dependency dependency : dependencies) {
      resolveDependency(dependency, workingDirectory);
    }
  }

  @Override
  public LogOutputStream getRedirectOutput(String prefix) {
    return LatexPluginLogOutputStream.toMavenDebug(getMojo().getLog(), prefix);
  }

  @Override
  public LogOutputStream getRedirectError(String prefix) {
    return LatexPluginLogOutputStream.toMavenError(getMojo().getLog(), prefix);
  }

  private AbstractMojo getMojo() {
    return mojo;
  }

  public MavenProject getProject() {
    return mojo.getProject();
  }

  private RepositorySystem getRepoSystem() {
    return mojo.getRepoSystem();
  }

  private RepositorySystemSession getRepoSession() {
    return mojo.getRepoSession();
  }

  private List<RemoteRepository> getRemoteRepos() {
    return mojo.getRemoteRepos();
  }

  public FileSet getResources() {
    return mojo.getResources();
  }

  private void resolveDependency(Dependency dependency, File workingDirectory) throws LatexExecutionException {
    Artifact artifact = new DefaultArtifact(dependency.getGroupId(), dependency.getArtifactId(), dependency.getClassifier(), dependency.getType(), dependency.getVersion());
    LocalArtifactRequest localRequest = new LocalArtifactRequest();
    localRequest.setArtifact(artifact);
    getLog().info(String.format("[mathan] resolving artifact %s from local", artifact));
    LocalArtifactResult localResult = getRepoSession().getLocalRepositoryManager().find(getRepoSession(), localRequest);
    if (localResult.isAvailable()) {
      try {
        extractArchive(localResult.getFile(), workingDirectory);
      } catch (IOException e) {
        throw new LatexExecutionException(String.format("Could not copy artifact %s", artifact), e);
      }
    } else {
      ArtifactRequest request = new ArtifactRequest();
      request.setArtifact(artifact);
      request.setRepositories(getRemoteRepos());
      getLog().info(String.format("[mathan] resolving artifact %s from %s", artifact, getRemoteRepos()));
      ArtifactResult result;
      try {
        result = getRepoSystem().resolveArtifact(getRepoSession(), request);
      } catch (ArtifactResolutionException e) {
        throw new LatexExecutionException(String.format("Could not resolve artifact %s", artifact), e);
      }
      if (result.isResolved()) {
        try {
          extractArchive(result.getArtifact().getFile(), workingDirectory);
        } catch (IOException e) {
          throw new LatexExecutionException(String.format("Could not copy artifact %s", artifact), e);
        }
      } else {
        throw new LatexExecutionException(String.format("Could not resolve artifact %s", artifact));
      }
    }

  }

  private void extractArchive(File archive, File workingDirectory) throws IOException {
    File archiveContent = Utils.extractArchive(archive);
    FileSetManager fileSetManager = new FileSetManager();

    getResources().setDirectory(archiveContent.getAbsolutePath());

    String[] includedFiles = fileSetManager.getIncludedFiles(getResources());
    for (String includedFile : includedFiles) {
      File src = new File(archiveContent, includedFile);
      File dest = new File(workingDirectory, includedFile);
      getLog().info(String.format("[mathan] including resource %s", includedFile));
      if (!dest.getParentFile().exists() && !dest.getParentFile().mkdirs()) {
        throw new IOException("Could not create directory " + dest.getParentFile().getAbsolutePath());
      }
      FileInputStream in = new FileInputStream(src);
      FileOutputStream out = new FileOutputStream(dest);
      IOUtils.copy(in, out);
      in.close();
      out.close();
    }
    FileUtils.deleteDirectory(archiveContent);
  }
}
