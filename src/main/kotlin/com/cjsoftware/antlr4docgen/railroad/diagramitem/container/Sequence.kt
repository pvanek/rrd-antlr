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

/**
 * List of items from left to right
 */
class Sequence(alignment: Alignment = Alignment.MIDDLE) : AbsDiagramContainer(alignment, "") {

    override fun calcWidth(): Float {
        return (sumOfChildren { it.calcWidth() }) + ((children.size - 1) * DiagramSettings.sequenceConnectorLength)
    }

    override fun calcHeight(): Float {
        return maxOfChildren { it.calcHeight() }
    }

    override fun entryLineOffsetY(): Float {
        return maxOfChildren { it.entryLineOffsetY() }
    }

    override fun renderAt(x: Float, y: Float, containerWidth: Float) = StringBuilder().apply {

        val myWidth = calcWidth()
        val space = startSpace(alignment, containerWidth, myWidth)

        var currentX = x + space
        val entryLineOffset = entryLineOffsetY()

        if (space > 0) {
            append(svgLine(x, y + entryLineY(entryLineOffset), x + space, y + entryLineY(entryLineOffset)))
        }

        children.forEachIndexed { count, item ->
            val childWidth = item.calcWidth()
            append(item.renderAt(currentX, y + entryLineOffset - item.entryLineOffsetY(), childWidth))
            currentX += childWidth

            if (count < children.size - 1) {
                append(
                    svgLine(
                        currentX,
                        y + entryLineY(entryLineOffset),
                        currentX + DiagramSettings.sequenceConnectorLength,
                        y + entryLineY(entryLineOffset)
                    )
                )
                currentX += DiagramSettings.sequenceConnectorLength
            }
        }

        if (currentX < (x + containerWidth)) {
            append(
                svgLine(
                    currentX,
                    y + entryLineY(entryLineOffset),
                    x + containerWidth,
                    y + entryLineY(entryLineOffset)
                )
            )
        }
        append("\n")
    }.toString()
}