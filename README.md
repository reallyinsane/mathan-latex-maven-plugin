![Maven metadata URI](https://maven-badges.herokuapp.com/maven-central/io.mathan.maven/mathan-latex-maven-plugin/badge.svg)
![example branch parameter](https://github.com/reallyinsane/mathan-latex-maven-plugin/actions/workflows/maven.yml/badge.svg)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/911e2266a08840daa9e95c99ab2f9ab4)](https://www.codacy.com/app/reallyinsane/mathan-latex-maven-plugin?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=reallyinsane/mathan-latex-maven-plugin&amp;utm_campaign=Badge_Grade)
<a href="https://opensource.org/licenses/Apache-2.0"><img src="https://img.shields.io/badge/license-apache2-blue.svg"></a>

# mathan-latex-maven-plugin / mathan-latex-gradle-plugin

Run LaTeX with this project either with Maven or Gradle.

## mathan-latex-maven-plugin

This is **THE** maven plugin to generate PDF, PS or DVI from LaTeX sources.

There have been some maven plugins around trying to provide an easy way to run LaTeX with maven. But it was never as easy as it should be. A lot of configuration had to be made to get the plugins working. And many of them are not maintained any more.

[**mathan-latex-maven-plugin**](maven.md) changes this. It is very easy to to use and there is little or no configuration needed for the default use cases.

### Usage
Using mathan-latex-maven-plugin introduces the packaging **pdf**. In a single maven module using mathan-latex-maven-plugin a PDF artifact is created.

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
      <version>1.0.4</version>
    </plugin>
  </plugins>
</build>
```

You can find more details about the configuration [**here**](maven.md).

## mathan-latex-gradle-plugin

This is **THE** alternative to generate PDF, PS, DVI from LaTeX sources with Gradle.
 
There have been a few gradle plugins around trying to provide a way to run LaTeX with gradle. There are not documented well and so it was not easy to use them.

For gradle [**mathan-latex-gradle-plugin**](gradle.md) changes this. It is very easy to to use and there is little or no configuration needed for the default use cases.

### Usage

After applying the plugin you can use the task **latex**.

```
buildscript {
    dependencies {
        classpath group: 'io.mathan.maven', name: 'mathan-latex-gradle-plugin',
                version: '1.0.4'
    }
}
apply plugin: 'io.mathan.latex'
``` 

You can find more details about the configuration [**here**](gradle.md).
