mathan-latex-gradle-plugin
=======================
This is **THE** alternative to generate PDF, PS, DVI from LaTeX sources with Gradle.
 
There have been a few gradle plugins around trying to provide a way to run LaTeX with gradle. There are not documented well and so it was not easy to use them.

For gradle **mathan-latex-gradle-plugin** changes this. It is very easy to to use and there is little or no configuration needed for the default use cases.

Usage
-----

After applying the plugin you can use the task **latex**.

```
buildscript {
    dependencies {
        classpath group: 'io.mathan.maven', name: 'mathan-latex-gradle-plugin',
                version: '1.0.0'
    }
}
apply plugin: 'io.mathan.latex'
``` 

Task
----
For execution of LaTeX just call the task **latex**.

Configuration
-------------
The following sections describe how to configure the plugin. All configuration can be done inside a *latex* configuration in the build.gradle.
```
latex {
  // configuration goes here
}
```

Tex source files
----------------
By default mathan-latex-gradle-plugin will search for a *.tex file in the source directory *src/main/tex*. The default behaviour can be changed using the configuration parameter *sourceDirectory*. 

*Example for using a non-default source directory.*
```
buildscript {
    dependencies {
        classpath group: 'io.mathan.maven', name: 'mathan-latex-gradle-plugin',
                version: '1.0.0'
    }
}
apply plugin: 'io.mathan.latex'

latex {
  sourceDirectory = 'src/main/custom'
}
``` 

LaTeX distribution
------------------
A LaTeX distribution is required to execute all LaTeX commands. By default mathan-latex-maven-plugin will assume that the bin directory of the LaTeX distribution is set on the **PATH** environment variable. As alternative the environment variable **texBin** can be used. It is also possible to use the configuration parameter *texBin* but this is not recommended as details about a local environment would become part of the build.gradle.

Dependencies
------------
To share resources (.bib, .tex, .eps, ...) for multiple maven modules they can be assembled in a separate module (can be jar or zip or whatever) used as a dependency for the maven project building you LaTeX document. Using the configuration parameter *resources* filters can be used to identify the resources to use.

Features
--------
The mathan-latex-gradle-plugin supports the output formats **PDF**, **PS** and **DVI**. The build process also includes the execution of

 - bibtex or biber
 - makeindex including makeindex style file
 - nomencl including nomencl style file

Logging / Debugging
-------------------
While building snapshot artifacts consider to set the configuration parameter *keepIntermediateFiles* to true to be able to review the latex files created withing the build process. You will find a file target/latex/mathan-latex-mojo.log containing the log output of all latex steps executed.

Configuration
-------------
The following configuration parameters can be used to change the default behaviour of the build.

Parameter|Description|Default
---------|-----------|-------
outputFormat|The desired output format. Can be either `dvi`, `ps` or `pdf`|`pdf`
sourceDirectory|Where to find *.tex documents.|`src/main/tex`
texBin|The bin directory of the tex distribution.|Searches on `PATH` environment and looks for system property `texBin`
texFile|Name of the main *.tex file to use| defaults to a single .tex file found in `sourceDirectory`
latexSteps|The latex commands to execute to generate the output document.|This is `['latex']` for `dvi`, `['latex', 'dvips']` for `ps` and `['pdflatex']` for `pdf`.
buildSteps|The build steps executed for a single document. The keyword `LaTeX` defines all steps configured with `latexSteps`| `['LaTeX', 'bibtex', 'makeindex', 'makeindexnomencl', 'LaTeX', 'LaTeX']`
steps|Configuration for user-defined steps.| none
keepIntermediateFile|Sets whether intermediate files created during the build should be kept.|`false`
makeIndexStyleFile|Name of the index style file to use for makeindex| none
makeIndexNomenclStyleFile|Name of the nomencl style file to use for makeindex| nomencl.ist from the TeX distribution
resources|A [FileTree](https://docs.gradle.org/current/javadoc/org/gradle/api/file/FileTree.html) defining the resources to include from given dependencies.| By default all files with the following extensions will be included: tex,cls,clo,sty,bib,bst,idx,ist,glo,eps,pdf
haltOnError|Sets whether the build should be stopped in case a single step finished with a non-zero exit code|true


Samples / Integration tests
---------------------------
The following integration tests are samples how to use mathan-latex-gradle-plugin also. All integration test are executed on [Travis](https://travis-ci.org/reallyinsane/mathan-latex-maven-plugin) - successfully.

Project|Description
-------|-----------
[configuration/resources](mathan-latex-it/src/test/resources/configuration/resources)| Sample using .bib resources from dependency only. 
[configuration/keepintermediatefiles](mathan-latex-it/src/test/resources/configuration/keepintermediatefiles)| Sample not removing intermediate files created.
[configuration/makeindexstylefile](mathan-latex-it/src/test/resources/configuration/makeindexstylefile)| Sample using a style file for makeindex.
[configuration/makeindexnomenclstylefile](mathan-latex-it/src/test/resources/configuration/makeindexnomenclstylefile)| Sample using a style file for makeindexnomencl.
[configuration/outputformat](mathan-latex-it/src/test/resources/configuration/outputformat)| Sample using all supported output formats.
[configuration/sourcedirectory](mathan-latex-it/src/test/resources/configuration/sourcedirectory)| Sample using custom source directory.
[configuration/texfile](mathan-latex-it/src/test/resources/configuration/texfile)| Sample specifying master tex file.
[dependencies/dependency](mathan-latex-it/src/test/resources/dependencies/dependency)| Dependency providing resource in a jar.
[dependencies/main](mathan-latex-it/src/test/resources/dependencies/main)| Sample using a resource from a dependency.
[dependencies/zip-dependency](mathan-latex-it/src/test/resources/dependencies/zip-dependency)| Dependency providing resource in a zip.
[dependencies/zip-main](mathan-latex-it/src/test/resources/dependencies/zip-main)| Sample using a resource from a zip dependency.
[features/biber](mathan-latex-it/src/test/resources/features/biber)| Sample using biber.
[features/bibtex](mathan-latex-it/src/test/resources/features/bibtex)| Sample using bibtex.
[features/makeindex](mathan-latex-it/src/test/resources/features/makeindex)| Sample using makeindex.
[features/nomenlc](mathan-latex-it/src/test/resources/features/nomenlc)| Sample using nomencl.

