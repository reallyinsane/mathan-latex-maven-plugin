[![Build Status](https://travis-ci.org/reallyinsane/mathan-latex-maven-plugin.svg?branch=master)](https://travis-ci.org/reallyinsane/mathan-latex-maven-plugin)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/911e2266a08840daa9e95c99ab2f9ab4)](https://www.codacy.com/app/reallyinsane/mathan-latex-maven-plugin?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=reallyinsane/mathan-latex-maven-plugin&amp;utm_campaign=Badge_Grade)
<a href="https://opensource.org/licenses/Apache-2.0"><img src="https://img.shields.io/badge/license-apache2-blue.svg"></a>

mathan-latex-maven-plugin
=======================
This is **THE** maven plugin to generate PDF, PS or DVI from LaTeX sources.

There have been some maven plugins around trying to provide an easy way to run LaTeX with maven. But it was never as easy as it should be. A lot of configuration had to be made to get the plugins working. And many of them are not maintained any more.

<i class=" icon-right-open"></i> **mathan-latex-maven-plugin** changes this. It is very easy to to use and there is little or no configuration needed for the defaul use cases.

<i class=" icon-right-open"></i> Usage {#usage}
------------------------------------------------------------------
Using mathan-latex-maven-plugin introduces the packaging **pdf**. Therefore in a single maven module using mathan-latex-maven-plugin a single PDF artifact is created. If you want to split the .tex sources or reuse them in multiple maven modules consider the [Dependencies](#dependencies) section.

*The minimal configuration looks like this:*
```
...
<packaging>pdf</packaging>
...
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
<i class=" icon-right-open"></i> Goal
----------------------------------------------------------------
If the packaging is set to **pdf** mathan-latex-maven-plugin will be executed in *package*, *install* and *deploy* phase. Otherwise the explizit goal *mathan:latex* can be used.

<i class=" icon-right-open"></i>Tex source files
------------------------------------------------------------------------------
By default mathan-latex-maven-plugin will search for a *.tex file in the source directory *src/main/tex*. The default behaviour can be changed using the configuration parameter *sourceDirectory*. Please note that for setting configuration parameters the *extensions* have to be activated.

*Example for using a non-default source directory.*
```
...
<plugin>
  <groupId>io.mathan.maven</groupId>
  <artifactId>mathan-latex-maven-plugin</artifactId>
  <version>0.9.0</version>
  <extensions>true</extensions>
  <configuration>
    <sourceDirectory>src/main/custom</sourceDirectory>
  </configuration>
</plugin>
```
<i class=" icon-right-open"></i>LaTeX distribution
----------------------------------------------------------------------------------
A LaTeX distribution is required to execute all LaTeX commands. By default mathan-latex-maven-plugin will assume that the bin directory of the LaTeX distribution is set on the **PATH** environment variable. As alternative the environment variable **texBin** can be used. It is also possible to use the configuration parameter *texBin* but this is not recommended as details about a local environment would become part of the Maven pom.xml.

<i class=" icon-right-open"></i>Dependencies
----------------------------------------------------------------------------------
To share resources (.bib, .tex, .eps, ...) for multiple maven modules they can be assembled in a jar module (zip modules to be supported soon) used as a dependency for the maven project building you LaTeX document. Using the configuration parameter *resources* filters can be used to identify the reources to use.

<i class=" icon-right-open"></i>Features
---------------------------------------------------------------------
The mathan-latex-maven-plugin supports the output formats **PDF**, **PS** and **DVI**. The build process also includes the execution of

 - bibtex or biber
 - makeindex including makeindex style file
 - nomencl (nomencl style file to be supported soon)

<i class=" icon-right-open"></i>Logging / Debugging
---------------------------------------------------------------------
While building snapshot artifacts consider to set the configuration parameter *keepIntermediateFiles* to true to be able to review the latex files created withing the build process. You will find a file target/latex/mathan-latex-mojo.log containing the log output of all latex steps executed.

<i class=" icon-cog"></i>Configuration
---------------------------
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


<i class=" icon-right-open"></i>Samples / Integration tests
-------------
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

