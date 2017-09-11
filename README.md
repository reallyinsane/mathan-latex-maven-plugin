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
You should place the .tex document in the source folder `src/main/tex`. You can also use sub folders - then a build will be executed for each sub folder. If so you can also define a sub folder `commons` to define common resources to be used for each build.

Configuration
-------------
The following configuration parameters can be used to change the default behaviour of the build.

Parameter|Description|Default
---------|-----------|-------
outputFormat|The desired output format. Can be either `dvi`, `ps` or `pdf`|`pdf`
texBin|The bin directory of the tex distribution.|Searches on `PATH` environment.
latexSteps|The latex commands to execute to generate the output document.|This is `latex` for `dvi`, `latex,dvips` for `ps` and `pdflatex` for `pdf`.
buildSteps|The build steps executed for a single document. The keyword `LaTeX` defines all steps configured with `latexSteps`|`LaTeX`, `bibtex`, `makeindex`, `makeindexnomencl`, `LaTeX`, `LaTeX`
steps|Configuration for user-defined steps.| none
keepIntermediateFile|Sets wether intermediate files created during the build should be kept.|`false`
sourceDirectory|Where to find *.tex documents.|'src/main/tex`
commonsDirectory|Name of the sub directory with common resources.|`commons`
makeIndexStyleFile|Name of the index style file to use for makeindex| none


