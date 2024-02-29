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

package com.cjsoftware.antlr4docgen.railroad.diagramitem.node

import com.cjsoftware.antlr4docgen.railroad.DiagramSettings
import com.cjsoftware.antlr4docgen.railroad.diagramitem.container.Alignment

internal class ParserRule(ruleName: String, alignment: Alignment = Alignment.MIDDLE) :
    AbsDiagramNode(alignment, ruleName, DiagramSettings.STYLE_PARSER) {
    override fun calcWidth(): Float {
        return caption.length * DiagramSettings.ruleCharSize + 2 * DiagramSettings.innerPadding
    }

    override fun renderAt(x: Float, y: Float, containerWidth: Float) = StringBuilder().apply {

        val rectWidth = calcWidth()
        val startRect = x + startSpace(alignment, containerWidth, rectWidth)

        append(svgLine(x, y + entryLineY(entryLineOffsetY()), startRect, y + entryLineY(entryLineOffsetY()), ""))
        append(
            svgAnchor(
                "#$caption",
                svgRectangle(startRect, y, rectWidth, DiagramSettings.itemHeight, styleClass)
            )
        )
        append(svgText(startRect + (rectWidth / 2), y + (DiagramSettings.itemHeight / 2), caption, styleClass))
        append(
            svgLine(
                startRect + rectWidth,
                y + entryLineY(entryLineOffsetY()),
                x + containerWidth,
                y + entryLineY(entryLineOffsetY()),
                ""
            )
        )
        append("\n")
    }.toString()
}