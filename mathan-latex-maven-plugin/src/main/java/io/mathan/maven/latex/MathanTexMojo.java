package io.mathan.maven.latex;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Simple Mojo to wrap all resources into a zip archive.
 */
@Mojo(name = "tex")
public class MathanTexMojo extends AbstractMojo {

    /**
     * Parameter defining the source directory to search for LaTeX documents.
     */
    @Parameter(defaultValue = "src/main/tex")
    private String sourceDirectory;

    /**
     * For injecting the current maven project.
     */
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        File source = new File(sourceDirectory);
        if (!source.exists()) {
            throw new MojoExecutionException(String.format("[mathan] source directory %s not found", sourceDirectory));
        }
        File targetDirectory = new File("target");
        if(!targetDirectory.exists()) {
            targetDirectory.mkdir();
        }
        File zip = new File(targetDirectory,project.getArtifactId()+"-"+project.getVersion()+".zip");
        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zip));
            File[] files = source.listFiles();
            for(File file:files) {
                if(!file.isDirectory()) {
                    FileInputStream in = new FileInputStream(file);
                    ZipEntry entry = new ZipEntry(file.getName());
                    out.putNextEntry(entry);
                    IOUtils.copy(in, out);
                    out.closeEntry();
                }
            }
            out.close();
            project.getArtifact().setFile(zip);
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException(String.format("[mathan] could not create file %s", zip.getName()));
        } catch (IOException e) {
            throw new MojoExecutionException(String.format("[mathan] could not write file %s", zip.getName()));
        }
    }
}
