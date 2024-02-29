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

package com.cjsoftware.antlr4docgen.railroad.diagramitem.container

import com.cjsoftware.antlr4docgen.railroad.DiagramSettings
import com.cjsoftware.antlr4docgen.railroad.diagramitem.AbsDiagramItem
import com.cjsoftware.antlr4docgen.railroad.diagramitem.SvgPathBuilder

/**
 * Repeated item (combine with optional to get "zero or more")
 */
internal class OneOrMore(val item: AbsDiagramItem, alignment: Alignment = Alignment.MIDDLE) :
    AbsDiagramItem(alignment, "") {

    override fun calcWidth(): Float {
        return item.calcWidth() + 2f * DiagramSettings.sequenceConnectorLength
    }

    override fun calcHeight(): Float {
        return item.calcHeight() + DiagramSettings.pathWidth + 2f * DiagramSettings.curveRadius
    }

    override fun entryLineOffsetY(): Float {
        return DiagramSettings.curveRadius + item.entryLineOffsetY()
    }

    override fun renderAt(x: Float, y: Float, containerWidth: Float) = StringBuilder().apply {
        val itemWidth = containerWidth - 2f * DiagramSettings.sequenceConnectorLength
        val loopOffset = DiagramSettings.sequenceConnectorLength / 4f

        val spaceStart = startSpace(alignment, containerWidth, calcWidth())
        if (spaceStart > 0) {
            append(svgLine(x, y + entryLineY(0f), x + spaceStart, y + entryLineY(0f)))
        }

        append(
            svgLine(
                x + spaceStart,
                y + entryLineY(entryLineOffsetY()),
                x + spaceStart + DiagramSettings.sequenceConnectorLength,
                y + entryLineY(entryLineOffsetY())
            )
        )

        append(
            item.renderAt(
                x + spaceStart + DiagramSettings.sequenceConnectorLength,
                y + DiagramSettings.curveRadius, itemWidth
            )
        )

        append(
            svgLine(
                x + spaceStart + DiagramSettings.sequenceConnectorLength + itemWidth,
                y + entryLineY(entryLineOffsetY()),
                x + containerWidth,
                y + entryLineY(entryLineOffsetY())
            )
        )

        val loopStretch = kotlin.math.max(item.entryLineOffsetY(), 0f)

        append(
            SvgPathBuilder.loopRight(
                x + spaceStart + DiagramSettings.sequenceConnectorLength - loopOffset,
                y + entryLineY(entryLineOffsetY()),
                x + spaceStart + DiagramSettings.sequenceConnectorLength + itemWidth + loopOffset,
                loopStretch
            ).build()
        )

        append(
            SvgPathBuilder.arrowLeft(
                x + (containerWidth / 2f),
                y + entryLineY(entryLineOffsetY()) - (2f * DiagramSettings.curveRadius + loopStretch + DiagramSettings.pathWidth),
                DiagramSettings.startSymSize
            ).build(DiagramSettings.STYLE_PATH_SYMBOL)
        )

    }.toString()
}