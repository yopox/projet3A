# Latex Presentation

## Introduction

You will find the presentation in the releases, but just in case, you can rebuild them as explained below. 

There are 2 presentations folders with the mid-year and end of year presentation, and an asset folder that contains all pictures and diagrams.

## Dependencies

Install a latex distribution. 

On linux, run : `sudo apt install texlive-full`

## Build

Go to the desired presentation folder, and run the following commands (including repetitions) :

```bash
pdflatex synthese
biber synthese
pdflatex synthese
pdflatex synthese
```
