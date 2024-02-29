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

internal class LexicalLiteral(literal: String, alignment: Alignment = Alignment.MIDDLE) :
    AbsDiagramNode(alignment, literal, DiagramSettings.STYLE_LEXER_LITERAL) {

    override fun calcWidth(): Float {
        return if (caption.length == 1) DiagramSettings.itemHeight + (2 * DiagramSettings.pathWidth)
        else java.lang.Float.max(
            caption.length * DiagramSettings.literalCharSize,
            DiagramSettings.itemHeight
        ) + (2 * DiagramSettings.innerPadding + 2 * DiagramSettings.pathWidth)
    }

    override fun renderAt(x: Float, y: Float, containerWidth: Float) = StringBuilder().apply {

        val myWidth = calcWidth()
        val spaceStart = startSpace(alignment, containerWidth, myWidth)

        if (spaceStart > 0) {
            append(svgLine(x, y + entryLineY(entryLineOffsetY()), x + spaceStart, y + entryLineY(entryLineOffsetY())))
        }

        if (caption.length > 1) {
            append(
                renderCapsuleContainer(
                    x + spaceStart + DiagramSettings.halfPathWidth,
                    y + DiagramSettings.halfPathWidth,
                    myWidth,
                    DiagramSettings.itemHeight
                )
            )
        } else {
            append(
                renderCircleContainer(
                    x + spaceStart + DiagramSettings.pathWidth,
                    y + DiagramSettings.halfPathWidth,
                    DiagramSettings.itemHeight
                )
            )
        }

        if (spaceStart + myWidth < containerWidth) {
            append(
                svgLine(
                    x + spaceStart + myWidth,
                    y + entryLineY(entryLineOffsetY()),
                    x + containerWidth,
                    y + entryLineY(entryLineOffsetY())
                )
            )
        }

        append("\n")
    }.toString()

    private fun renderCircleContainer(x: Float, y: Float, size: Float) = StringBuilder().apply {
        append(svgCircle((x + size / 2), (y + size / 2), size / 2 + DiagramSettings.halfPathWidth, styleClass))
        append(svgText((x + size / 2), (y + size / 2), caption, styleClass))
    }.toString()

    private fun renderCapsuleContainer(x: Float, y: Float, width: Float, height: Float) = StringBuilder().apply {
        append(svgCapsule(x, y, width, height, styleClass))
        append(svgText(x + width / 2, y + height / 2, caption, styleClass))
    }.toString()
}