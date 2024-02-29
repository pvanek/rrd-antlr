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
import com.cjsoftware.antlr4docgen.railroad.diagramitem.SvgPathBuilder

/**
 * List of possible alternatives, stacked top to bottom.
 */
internal class Alternatives(alignment: Alignment = Alignment.MIDDLE) : AbsDiagramContainer(alignment, "") {

    override fun calcWidth(): Float {
        return maxOfChildren { it.calcWidth() } + 2f * DiagramSettings.curveRadius + 2f * DiagramSettings.sequenceConnectorLength
    }

    override fun calcHeight(): Float {
        return sumOfChildren { it.calcHeight() - it.entryLineOffsetY() + DiagramSettings.sequenceConnectorLength / 2f }
    }

    override fun entryLineOffsetY(): Float {
        return children[0].entryLineOffsetY()
    }

    override fun renderAt(x: Float, y: Float, containerWidth: Float) = StringBuilder().apply {

        val myWidth = calcWidth()
        val space = startSpace(alignment, containerWidth, myWidth)
        val childContainerWidth = myWidth - 2 * DiagramSettings.curveRadius

        var currentY = y
        var prevY = y

        children.forEachIndexed { count, item ->

            append(item.renderAt(x + space + DiagramSettings.curveRadius, currentY, childContainerWidth))

            val itemHeight = item.calcHeight()

            if (count == 0) {
                append(
                    svgLine(
                        x,
                        y + entryLineY(item.entryLineOffsetY()),
                        x + space + DiagramSettings.curveRadius,
                        y + entryLineY(item.entryLineOffsetY())
                    )
                )
                append(
                    svgLine(
                        x + space + childContainerWidth + DiagramSettings.curveRadius,
                        y + entryLineY(item.entryLineOffsetY()),
                        x + containerWidth,
                        y + entryLineY(item.entryLineOffsetY())
                    )
                )
            } else {
                val yei = prevY + if (count == 1) entryLineY(entryLineOffsetY()) else -DiagramSettings.pathWidth
                val yeo = currentY + entryLineY(item.entryLineOffsetY())
                append(
                    SvgPathBuilder.enterLeft(
                        x + space,
                        yei,
                        x + space + DiagramSettings.curveRadius,
                        yeo
                    ).build()
                )

                append(
                    SvgPathBuilder.arrowDown(
                        x + space,
                        ((yei + yeo) / 2f),
                        DiagramSettings.startSymSize
                    ).build(DiagramSettings.STYLE_PATH_SYMBOL)
                )

                append(
                    SvgPathBuilder.exitRight(
                        x + space + childContainerWidth + DiagramSettings.curveRadius,
                        yeo,
                        x + containerWidth,
                        yei
                    ).build()
                )
            }
            prevY = currentY
            currentY += (itemHeight - item.entryLineOffsetY() + DiagramSettings.sequenceConnectorLength / 2f)
        }

    }.toString()
}