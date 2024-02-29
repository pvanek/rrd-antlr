/*
 * Copyright (c) 2022 Chris James.
 *    This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.cjsoftware.antlr4docgen.railroad

import com.cjsoftware.antlr4docgen.model.Rule
import com.cjsoftware.antlr4docgen.railroad.DiagramSettings.processStyleFile
import com.cjsoftware.antlr4docgen.railroad.diagramitem.container.Sequence
import java.nio.file.Path
import java.nio.file.Paths

internal class HtmlDiagram : IDiagram {

    private val styles =
        processStyleFile(
            this::class.java.classLoader.getResource("default_styles.txt")!!.readText(),
        )

    override fun render(rootPath: Path, ruleDiagramList: List<Pair<Rule, Sequence>>) {
        val content = render(ruleDiagramList)
        val indexPath = Paths.get(rootPath.toString(), "index.html")
        val f = indexPath.toFile()
        f.writeText(content)
    }

    fun render(ruleDiagramList: List<Pair<Rule, Sequence>>) = StringBuilder().apply {
        append(documentPrefix("generated docs", styleEmbed(styles)))

        ruleDiagramList.forEach {
            append(renderRule(it.first, it.second))
        }

        append(documentSuffix())

    }.toString()

    private fun renderRule(rule: Rule, sequence: Sequence) = StringBuilder().apply {
        val height = sequence.calcHeight()
        val width = sequence.calcWidth()
        append("<div id=\"${rule.name}\" class=\"${DiagramSettings.STYLE_RULE_SECTION}\">")
        append(
            diagramPrefix(
                height + 2 * DiagramSettings.itemHeight,
                width + 2 * DiagramSettings.itemHeight,
                rule.name
            )
        )
        append(
            sequence.renderAt(
                DiagramSettings.itemHeight, DiagramSettings.itemHeight,
                width
            )
        )
        append(diagramSuffix())
        append(ruleCrossRef(rule))

        append("</div>")
    }

    private fun styleEmbed(styleDefs: String) = StringBuilder().apply {
        append("<style>\n")
        append(styleDefs)
        append("</style>\n")
    }.toString()

    private fun documentPrefix(title: String, styleEmbed: String) = StringBuilder().apply {
        append(
            """
            <html xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">
            <head>
            ${styleEmbed}
            <title>$title</title>
            <h1>$title</h1>
            </head>
            <body>
        """.trimIndent()
        )
    }.toString()

    private fun documentSuffix() = StringBuilder().apply {
        append(
            """
            </body>
            </html>
        """.trimIndent()
        )
    }.toString()

    private fun diagramPrefix(height: Float, width: Float, id: String) = StringBuilder().apply {
        append("<svg class=\"${DiagramSettings.STYLE_DIAGRAM}\" width=\"$width\" height=\"$height\" viewBox=\"0 0 $width $height\" id=\"$id\">\n")
    }.toString()

    private fun diagramSuffix() = StringBuilder().apply {
        append("</svg></br>\n")
    }.toString()

    private fun ruleCrossRef(rule: Rule) = StringBuilder().apply {
        if (rule.referencedBy.isNotEmpty()) {
            append("<div class=\"${DiagramSettings.STYLE_CROSS_REFERENCE}\">")
            append("<p class=\"${DiagramSettings.STYLE_CROSS_REFERENCE}\"><i>${rule.name}</i> is referenced by:</p>")
            rule.referencedBy.sortedBy { it.name }.forEach {
                append("<a href=\"#${it.name}\" class=\"${DiagramSettings.STYLE_CROSS_REFERENCE}\">${it.name}</a> ")
            }
            append("</div>")
        }
    }.toString()
}