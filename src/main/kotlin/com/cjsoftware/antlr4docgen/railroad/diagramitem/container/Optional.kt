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
 * Optional item
 */
internal class Optional(val item: AbsDiagramItem, alignment: Alignment = Alignment.MIDDLE) :
    AbsDiagramItem(alignment, "") {

    override fun calcWidth(): Float {
        return item.calcWidth() + (DiagramSettings.curveRadius * 4f)
    }

    override fun calcHeight(): Float {
        return item.calcHeight() + 2f * DiagramSettings.curveRadius + DiagramSettings.sequenceConnectorLength
    }

    override fun renderAt(x: Float, y: Float, containerWidth: Float) = StringBuilder().apply {
        val myWidth = calcWidth()
        val childContainerWidth = myWidth - (DiagramSettings.curveRadius * 4f)

        append(svgLine(x, y + entryLineY(0f), x + containerWidth, y + entryLineY(0f)))
        append(
            SvgPathBuilder.arrowRight(
                x + containerWidth / 2f,
                y + entryLineY(0f),
                DiagramSettings.startSymSize
            ).build(DiagramSettings.STYLE_PATH_SYMBOL)
        )

        val mySpace = startSpace(alignment, containerWidth, myWidth)

        append(
            item.renderAt(
                x + mySpace + 2f * DiagramSettings.curveRadius,
                y + 2f * DiagramSettings.curveRadius + DiagramSettings.sequenceConnectorLength,
                childContainerWidth
            )
        )

        val ySi = y + entryLineY(0f)
        val ySo =
            y + 2f * DiagramSettings.curveRadius + entryLineY(item.entryLineOffsetY()) + +DiagramSettings.sequenceConnectorLength

        append(
            SvgPathBuilder.leftEntrySnake(
                x + mySpace,
                ySi,
                x + mySpace + (2f * DiagramSettings.curveRadius),
                ySo
            ).build()
        )

        append(
            SvgPathBuilder.arrowDown(
                x + mySpace + DiagramSettings.curveRadius,
                (ySi + ySo) / 2f,
                DiagramSettings.startSymSize
            ).build(DiagramSettings.STYLE_PATH_SYMBOL)
        )

        append(
            SvgPathBuilder.rightExitSnake(
                x + mySpace + childContainerWidth + (4f * DiagramSettings.curveRadius),
                ySi,
                x + mySpace + childContainerWidth + (2 * DiagramSettings.curveRadius),
                ySo
            ).build()
        )
        append("\n")
    }.toString()

}