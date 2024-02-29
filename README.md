# rrd-antlr: RRD generator for ANTLR4 grammars

> :warning: **This is work in progress!**

This tool is capable to generate RRD in various formats:

* HTML (with SVG)
* Antora/Asciidoc (with SVG, [pending problem](https://gitlab.com/antora/antora/-/issues/1001) -- ImageMagick can be used to convert SVG to eg. PNG until this problem will be fixed)

## Usage:

```
java -jar <options> <sourcegrammarfile> <targetdirectory>
```

Options:

* `--type` -- one of supported formats: `html`, `antora`
  
