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

import java.io.InputStream

private const val REPLACEMENT_TAG_TEMPLATE = "${'$'}{%s}"

object DiagramSettings {
    const val STYLE_DIAGRAM = "rrd"
    const val STYLE_PARSER = "parser"
    const val STYLE_LEXER = "lexer"
    const val STYLE_LEXER_LITERAL = "lexer-literal"
    const val STYLE_RULE_START = "rule-start"
    const val STYLE_RULE_END = "rule-end"
    const val STYLE_CROSS_REFERENCE = "cross-reference"
    const val STYLE_PATH_SYMBOL = "symbol"
    const val STYLE_RULE_SECTION = "rule-section"

    private val values = mutableMapOf<String, Float>().apply {
        this["CURVE_RADIUS"] = 10f
        this["INNER_PADDING"] = 10f
        this["PATH_WIDTH"] = 2f
        this["RULE_START_CHAR_SIZE"] = 14f
        this["RULE_CHAR_SIZE"] = 12f
        this["LITERAL_CHAR_SIZE"] = 12f
        this["SEQUENCE_CONNECTOR_LEN"] = requireNotNull(this["CURVE_RADIUS"])
        this["ITEM_HEIGHT"] = requireNotNull(this["CURVE_RADIUS"]) * 2f
    }

    var curveRadius
        get() = requireNotNull(values["CURVE_RADIUS"])
        set(value) {
            values["CURVE_RADIUS"] = value
        }

    var innerPadding
        get() = requireNotNull(values["INNER_PADDING"])
        set(value) {
            values["INNER_PADDING"] = value
        }

    var pathWidth
        get() = requireNotNull(values["PATH_WIDTH"])
        set(value) {
            values["PATH_WIDTH"] = value
        }

    var ruleStartCharSize
        get() = requireNotNull(values["RULE_START_CHAR_SIZE"])
        set(value) {
            values["RULE_START_CHAR_SPACE"] = value
        }

    var ruleCharSize
        get() = requireNotNull(values["RULE_CHAR_SIZE"])
        set(value) {
            values["RULE_CHAR_SIZE"] = value
        }

    var literalCharSize
        get() = requireNotNull(values["LITERAL_CHAR_SIZE"])
        set(value) {
            values["LITERAL_CHAR_SIZE"] = value
        }

    var sequenceConnectorLength
        get() = requireNotNull(values["SEQUENCE_CONNECTOR_LEN"])
        set(value) {
            values["SEQUENCE_CONNECTOR_LEN"] = value
        }

    var itemHeight
        get() = requireNotNull(values["ITEM_HEIGHT"])
        set(value) {
            values["ITEM_HEIGHT"] = value
        }

    val halfItemHeight
        get() = itemHeight / 2f

    val halfPathWidth
        get() = pathWidth / 2f

    val startSymSize
        get() = halfItemHeight

    val endSymSize
        get() = curveRadius / 2f

    private fun processReplacementTags(input: String): String {
        var output = input
        values.forEach {
            output = output.replace(REPLACEMENT_TAG_TEMPLATE.format(it.key), it.value.toString(), true)
        }

        return output
    }


    fun processStyleFile(input: String): String {
        return StringBuilder().apply {
            input.split("\n").forEach {
                append(processReplacementTags(it))
                append("\n")
            }
        }.toString()
    }
}

