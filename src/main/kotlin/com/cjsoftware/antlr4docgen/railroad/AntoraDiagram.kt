package com.cjsoftware.antlr4docgen.railroad

import com.cjsoftware.antlr4docgen.model.Rule
import com.cjsoftware.antlr4docgen.railroad.diagramitem.container.Sequence
import java.nio.file.Path
import java.nio.file.Paths

internal class AntoraDiagram(
) : IDiagram {

    override fun render(rootPath: Path, ruleDiagramList: List<Pair<Rule, Sequence>>) {
        val rootPathString = rootPath.toString()
        val partialsPath = Paths.get(rootPathString, "partials", "grammar_rules")
        val imagesPath = Paths.get(rootPathString, "images", "grammar_rules")
        partialsPath.toFile().mkdirs()
        imagesPath.toFile().mkdirs()

        val page = StringBuilder().apply {
            ruleDiagramList.forEach {
                append(
                    renderRule(
                        imagesPath,
                        it.first,
                        it.second,
                    ),
                )
            }
        }.toString()
        val indexPath = Paths.get(rootPathString, "partials", "syntax.adoc")
        indexPath.toFile().writeText(page)
    }

    private fun renderRule(imagesPath: Path, rule: Rule, sequence: Sequence) = StringBuilder().apply {
        val height = sequence.calcHeight()
        val width = sequence.calcWidth()
        append("=== ${rule.name}\n\n")
        append("// optional manually written docs for this rule\n")
        append("include::partial${'$'}grammar_rules/${rule.name}.adoc[opts=optional]\n\n")
        append(".Syntax\n\n")
        append("image:grammar_rules/${rule.name}.svg.png[RRD for ${rule.name}]\n\n")

        val imagePath = Paths.get(imagesPath.toString(), "${rule.name}.svg")
        val f = imagePath.toFile()
        f.writeText(
            diagramPrefix(
                height + 2 * DiagramSettings.itemHeight,
                width + 2 * DiagramSettings.itemHeight,
                rule.name,
            ),
        )
        f.appendText(applyCss())
        f.appendText(
            sequence.renderAt(
                DiagramSettings.itemHeight,
                DiagramSettings.itemHeight,
                width,
            ),
        )
        f.appendText(diagramSuffix())

        if (rule.referencedBy.isNotEmpty()) {
            append(".Referenced By:\n\n")
            rule.referencedBy.sortedBy { it.name }.forEach {
                append("* xref:#_${it.name}[${it.name}]\n")
            }
        }

        append("\n\n")
    }

    private fun diagramPrefix(height: Float, width: Float, id: String) = StringBuilder().apply {
        append("<svg class=\"${DiagramSettings.STYLE_DIAGRAM}\" width=\"$width\" height=\"$height\" viewBox=\"0 0 $width $height\" id=\"$id\">\n")
    }.toString()

    private fun diagramSuffix() = StringBuilder().apply {
        append("</svg>\n")
    }.toString()

    private fun applyCss(): String {
        val PATH_WIDTH = 1
        val RULE_START_CHAR_SIZE = 12
        val RULE_CHAR_SIZE = 12
        val LITERAL_CHAR_SIZE = 12
        return """
            <style>
        svg.rrd line {
            stroke-width:$PATH_WIDTH;
            stroke:black;
            fill:rgba(0,0,0,0);
        }

        svg.rrd path {
            stroke-width:$PATH_WIDTH;
            stroke:black;
            fill:rgba(0,0,0,0);
        }

        svg.rrd path.symbol {
            stroke-width:1;
            stroke:black;
            fill:black;
        }

        svg.rrd circle.rule-start{
            stroke-width:$PATH_WIDTH;
            stroke:black;
            fill:rgb(246, 200, 200);
        }

        svg.rrd rect.rule-start{
            stroke-width:$PATH_WIDTH;
            stroke:black;
            fill:rgb(246, 200, 200);
        }

        svg.rrd text.rule-start {
            font:italic ${RULE_START_CHAR_SIZE}px monospace;
            text-anchor:middle;
        }

        svg.rrd path.rule-end {
            stroke-width:$PATH_WIDTH;
            stroke:black;
            fill:rgba(0,0,0,0);
        }

        svg.rrd rect.lexer {
            stroke-width:$PATH_WIDTH;
            stroke:black;
            fill:rgb(246, 255, 204);
        }

        svg.rrd text.lexer {
            font:italic ${RULE_CHAR_SIZE}px monospace;
            text-anchor:middle;
        }

        svg.rrd rect.lexer-literal {
            stroke-width:$PATH_WIDTH;
            stroke:black;
            fill:rgb(246, 255, 204);
        }

        svg.rrd circle.lexer-literal {
            stroke-width:$PATH_WIDTH;
            stroke:black;
            fill:rgb(246, 255, 204);
        }

        svg.rrd text.lexer-literal {
            font:bold ${LITERAL_CHAR_SIZE}px monospace;
            text-anchor:middle;
        }

        svg.rrd rect.parser {
            stroke-width:$PATH_WIDTH;
            stroke:black;
            fill:hsl(120,100%,90%);
        }

        svg.rrd text.parser {
            font:italic ${RULE_CHAR_SIZE}px monospace;
            text-anchor:middle;
        }
            </style>
        """
    }
}
