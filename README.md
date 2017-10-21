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
      <version>1.0.0</version>
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
keepIntermediateFile|Sets wether intermediate files created during the build should be kept.|`false`
makeIndexStyleFile|Name of the index style file to use for makeindex| none
dependencyIncludes|List of file extensions to be included from dependencies.| tex,cls,clo,sty,bib,bst,idx,ist,glo,eps,pdf

Dependencies
------------
In case you want to use resources for multiple latex project you can declare dependencies. Using the property
`dependencyIncludes` resources from the artifact of the dependencies (at the moment .jar files only) will be included
during the latex build. 

Samples / Integration tests
---------------------------
The following integration tests are samples how to use mathan-latex-maven-plugin also.

Project|Description
-------+-----------
[configuration/dependencyincludes](mathan-latex-maven-plugin-it/src/test/resources/configuration/dependencyincludes)| 
[configuration/keepintermediatefiles](mathan-latex-maven-plugin-it/src/test/resources/configuration/keepintermediatefiles)|
[configuration/makeindexstylefile](mathan-latex-maven-plugin-it/src/test/resources/configuration/makeindexstylefile)|
[configuration/outputformat](mathan-latex-maven-plugin-it/src/test/resources/configuration/outputformat)|
[configuration/sourcedirectory](mathan-latex-maven-plugin-it/src/test/resources/configuration/sourcedirectory)|
[configuration|texfile](mathan-latex-maven-plugin-it/src/test/resources/configuration|texfile)|
[dependencies/dependency](mathan-latex-maven-plugin-it/src/test/resources/dependencies/dependency)|
[dependencies/main](mathan-latex-maven-plugin-it/src/test/resources/dependencies/main)|
[features/biber](mathan-latex-maven-plugin-it/src/test/resources/features/biber)|
[features/bibtex](mathan-latex-maven-plugin-it/src/test/resources/features/bibtex)|
[features/makeindex](mathan-latex-maven-plugin-it/src/test/resources/features/makeindex)|
[features/nomenlc](mathan-latex-maven-plugin-it/src/test/resources/features/nomenlc)|


