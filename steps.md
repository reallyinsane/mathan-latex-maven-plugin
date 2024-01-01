Steps of LaTeX processing
=======================

The execution of processing LaTeX documents is build up on steps. There are steps responsible for paring tex documents and creating output/intermediate files and steps processing additional files like bib, idx, nlo.
Each step is executed by calling a external command with a given set of parameters. The following table lists all steps with their default configuration.

ID|Command|Input format|Output format|Arguments|Optional|Log extension
--|-------|------------|-------------|---------|--------|-------------
latex|latex|tex|dvi|-interaction=nonstopmode --src-specials %input|false|log 
pdflatex|pdflatex|tex|pdf|-synctex=1 -interaction=nonstopmode --src-specials %input|false|log
xelatex|xelatex|tex|pdf|-synctex=1 -interaction=nonstopmode  %input|false|log
lulatex|lulatex|tex|pdf|-synctex=1 -interaction=nonstopmode --src-specials %input|false|log
bibtex|bibtex|bib|aux|%base|true|blg
biber|biber|bib|bbl|%base|true|blg
makeindex|makeindex|idx|ind|%input -s %style|true|ilg
dvips|dvips|dvi|ps|-R0 -o %output %input|false|log
dvipdfm|dvipdfm|dvi|pdf|%input|false|log
ps2pdf|ps2pdf|ps|pdf|%input|false|log
makeindexnomencl|makeindex|nlo|nls|%input -s %style -o %output|true|ilg

- ID: the unique ID of the step (which is used in `latexSteps` or `buildSteps` configuration
- Command: the command to execute
- Input format: the file extension of the input document for the command
- Output format: the file extension of the output document created by the command
- Arguments: the arguments for the command execution
- Optional: if true the processing will stop if the command execution was not successful (return code !=0)
- Log extension: the file extension of the log file created by the command

Cusomizing steps
----------------

The step configuration can be overriden in the configuration of the Maven or Gradle plugin. It is also possible to add additional steps to be executed in the process.

The sample [configuration/xelatex](mathan-latex-it/src/test/resources/configuration/xelatex) demonstrate how to change the arguments to the xelatex command.

