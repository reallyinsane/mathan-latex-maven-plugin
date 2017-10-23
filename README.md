[![Build Status](https://travis-ci.org/reallyinsane/mathan-latex-maven-plugin.svg?branch=master)](https://travis-ci.org/reallyinsane/mathan-latex-maven-plugin)
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
To run the build using LaTeX just use the goal:
```
mvn mathan:latex
```

Tex sources
-----------
You should place the .tex document in the source folder `src/main/tex`. 

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


