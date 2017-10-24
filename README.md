[![Build Status](https://travis-ci.org/reallyinsane/mathan-latex-maven-plugin.svg?branch=master)](https://travis-ci.org/reallyinsane/mathan-latex-maven-plugin)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/911e2266a08840daa9e95c99ab2f9ab4)](https://www.codacy.com/app/reallyinsane/mathan-latex-maven-plugin?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=reallyinsane/mathan-latex-maven-plugin&amp;utm_campaign=Badge_Grade)
<a href="https://opensource.org/licenses/Apache-2.0"><img src="https://img.shields.io/badge/license-apache2-blue.svg"></a>

# mathan-latex-maven-plugin
Maven plugin to generate PDF/PS/DVI from LaTeX sources.

There have been some maven plugins already with the aim to run LaTeX with maven. But is was never as easy as it should be.
So this maven plugin changes this as the plugin will assume a lot of defaults for the main use cases. To use the maven plugin just declare it in the plugin section of your pom.xml.

```xml
<build>
  <plugins>
    <plugin>
      <groupId>io.mathan.maven</groupId>
      <artifactId>mathan-latex-maven-plugin</artifactId>
      <version>0.9.0</version>
    </plugin>
  </plugins>
</build>
```

Goal
----
To run the build just use the goal:
```
mvn mathan:latex
```

LaTeX distribution
------------------
The maven plugin uses the executables of an existing LaTeX distribution. If the bin directory of this distribution is on the `PATH` environment variable, nothing has to be configured. You can change this using the configuration parameter `texBin` or using a environment variable named `texBin`.

Tex sources
-----------
You should place the .tex document in the source folder `src/main/tex`. This is the default directory, you can change it the behaviour with the configuration parameter `sourceDirectory`. If there is only one .tex file in this directory this is used as input file. If you have multiple .tex files you have to specify the main text file using the configuration parameter `texFile`.

Common resources
----------------
If you want to share resources (.bib, .tex, .eps, etc.) for certain projects you can assemble them into a jar and use this as a dependency. Resources specified using the configuration parameter `resource` will be used for the build process.

Features
--------
By default in addition to the pure latex commands (depending on the desired output format) bibtex, makeindex and makeindex with nomencl are supported. Bibtex can also be replaced by biber. For makeindex a style file can be configured. (later for nomencl too)

Logging
-------
While building snapshot artifacts consider to set the configuration parameter `keepIntermediateFiles` to  `true` to be able to review the latex files created withing the build process. You will find a file `target/latex/mathan-latex-mojo.log` containing the log output of all latex steps executed.

Configuration
-------------
The following configuration parameters can be used to change the default behaviour of the build.

Parameter|Description|Default
---------|-----------|-------
outputFormat|The desired output format. Can be either `dvi`, `ps` or `pdf`|`pdf`
sourceDirectory|Where to find *.tex documents.|'src/main/tex`
texBin|The bin directory of the tex distribution.|Searches on `PATH` environment and looks for system property `texBin`
texFile|Name of the main *.tex file to use| defaults to a single .tex file found in `sourceDirectory`
latexSteps|The latex commands to execute to generate the output document.|This is `latex` for `dvi`, `latex,dvips` for `ps` and `pdflatex` for `pdf`.
buildSteps|The build steps executed for a single document. The keyword `LaTeX` defines all steps configured with `latexSteps`|`LaTeX`, `bibtex`, `makeindex`, `makeindexnomencl`, `LaTeX`, `LaTeX`
steps|Configuration for user-defined steps.| none
keepIntermediateFile|Sets whether intermediate files created during the build should be kept.|`false`
makeIndexStyleFile|Name of the index style file to use for makeindex| none
resources|A [FileSet](https://maven.apache.org/shared/file-management/apidocs/org/apache/maven/shared/model/fileset/FileSet.html) defining the resources to include from given dependencies.| By default all files with the following extensions will be included: tex,cls,clo,sty,bib,bst,idx,ist,glo,eps,pdf
haltOnError|Sets whether the build should be stopped in case a single step finished with a non-zero exit code|true

Dependencies
------------
In case you want to use resources for multiple latex project you can declare dependencies. The configuration property
`resources` is a [FileSet](https://maven.apache.org/shared/file-management/apidocs/org/apache/maven/shared/model/fileset/FileSet.html).
You can configure it like described [here](https://maven.apache.org/shared/file-management/examples/mojo.html).

Samples / Integration tests
---------------------------
The following integration tests are samples how to use mathan-latex-maven-plugin also.

Project|Description
-------|-----------
[configuration/dependencyincludes](mathan-latex-maven-plugin-it/src/test/resources/configuration/dependencyincludes)| Sample using .bib resources from dependency only. 
[configuration/keepintermediatefiles](mathan-latex-maven-plugin-it/src/test/resources/configuration/keepintermediatefiles)| Sample not removing intermediate files created.
[configuration/makeindexstylefile](mathan-latex-maven-plugin-it/src/test/resources/configuration/makeindexstylefile)| Sample using a style file for makeindex.
[configuration/outputformat](mathan-latex-maven-plugin-it/src/test/resources/configuration/outputformat)| Sample using all supported output formats.
[configuration/sourcedirectory](mathan-latex-maven-plugin-it/src/test/resources/configuration/sourcedirectory)| Sample using custom source directory.
[configuration/texfile](mathan-latex-maven-plugin-it/src/test/resources/configuration/texfile)| Sample specifying master tex file.
[dependencies/dependency](mathan-latex-maven-plugin-it/src/test/resources/dependencies/dependency)| Dependency providing resource in a jar.
[dependencies/main](mathan-latex-maven-plugin-it/src/test/resources/dependencies/main)| Sample using a resource from a dependency.
[features/biber](mathan-latex-maven-plugin-it/src/test/resources/features/biber)| Sample using biber.
[features/bibtex](mathan-latex-maven-plugin-it/src/test/resources/features/bibtex)| Sample using bibtex.
[features/makeindex](mathan-latex-maven-plugin-it/src/test/resources/features/makeindex)| Sample using makeindex.
[features/nomenlc](mathan-latex-maven-plugin-it/src/test/resources/features/nomenlc)| Sample using nomencl.


