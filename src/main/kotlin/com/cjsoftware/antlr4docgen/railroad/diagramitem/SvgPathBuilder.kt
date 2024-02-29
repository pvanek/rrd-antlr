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

package com.cjsoftware.antlr4docgen.railroad.diagramitem

import com.cjsoftware.antlr4docgen.railroad.DiagramSettings

class SvgPathBuilder {
    private val instructions = mutableListOf<String>()

    fun moveAbs(x: Float, y: Float) = this.apply {
        instructions.add("M $x $y ")
    }

    fun hline(dist: Float) {
        instructions.add("h $dist")
    }

    fun vline(dist: Float) {
        instructions.add("v $dist")
    }

    fun lineTo(x: Float, y: Float) = this.apply {
        instructions.add("l $x $y ")
    }

    fun lineToAbs(x: Float, y: Float) = this.apply {
        instructions.add("L $x $y ")
    }

    fun arcTo(cw: Boolean, dx: Float, dy: Float) = this.apply {
        instructions.add("a ${DiagramSettings.curveRadius} ${DiagramSettings.curveRadius} 0 0 ${if (cw) 1 else 0} $dx $dy ")
    }

    fun closePath() {
        instructions.add("z ")
    }

    fun build(svgClass: String = "") = StringBuilder().apply {
        append("<path d=\"")
        instructions.forEach {
            append(it)
        }
        if (svgClass.isNotBlank()) append("\" class=\"$svgClass\"/>") else append("\"/>")
    }.toString()

    companion object {
        fun leftEntrySnake(xi: Float, yi: Float, xo: Float, yo: Float) = SvgPathBuilder().apply {
            moveAbs(xi, yi)
            arcTo(true, DiagramSettings.curveRadius, DiagramSettings.curveRadius)
            vline(yo - yi - 2f * DiagramSettings.curveRadius)
            arcTo(false, DiagramSettings.curveRadius, DiagramSettings.curveRadius)
            lineToAbs(xo, yo)
        }

        fun rightExitSnake(xi: Float, yi: Float, xo: Float, yo: Float) = SvgPathBuilder().apply {
            moveAbs(xi, yi)
            arcTo(false, -DiagramSettings.curveRadius, DiagramSettings.curveRadius)
            vline(yo - yi - 2f * DiagramSettings.curveRadius)
            arcTo(true, -DiagramSettings.curveRadius, DiagramSettings.curveRadius)
            lineToAbs(xo, yo)
        }

        fun loopRight(xi: Float, yi: Float, xo: Float, yStretch: Float = 0f) =
            SvgPathBuilder().apply {
                moveAbs(xi, yi)
                arcTo(true, -DiagramSettings.curveRadius, -DiagramSettings.curveRadius)
                vline(-(DiagramSettings.pathWidth + yStretch))
                arcTo(true, DiagramSettings.curveRadius, -DiagramSettings.curveRadius)
                hline(xo - xi)
                arcTo(true, DiagramSettings.curveRadius, DiagramSettings.curveRadius)
                vline(DiagramSettings.pathWidth + yStretch)
                arcTo(true, -DiagramSettings.curveRadius, DiagramSettings.curveRadius)
            }

        fun enterLeft(xi: Float, yi: Float, xo: Float, yo: Float) = SvgPathBuilder().apply {
            moveAbs(xi, yi)
            (yo - yi - DiagramSettings.curveRadius).let {
                if (it > 0) vline(it)
            }
            arcTo(false, DiagramSettings.curveRadius, DiagramSettings.curveRadius)
            (xo - xi - DiagramSettings.curveRadius).let {
                if (it > 0) hline(it)
            }
        }

        fun exitRight(xi: Float, yi: Float, xo: Float, yo: Float) = SvgPathBuilder().apply {
            moveAbs(xi, yi)
            (xo - xi - DiagramSettings.curveRadius).let {
                if (it > 0) hline(it)
            }
            arcTo(false, DiagramSettings.curveRadius, -DiagramSettings.curveRadius)
            (yi - yo - DiagramSettings.curveRadius).let {
                if (it > 0) vline(-it)
            }

        }

        fun arrowRight(x: Float, y: Float, size: Float) = SvgPathBuilder().apply {
            val s2 = size / 2f
            val s4 = size / 4f
            moveAbs(x + size / 2, y)
            lineTo(-size, -s2)
            lineTo(s4, s2)
            lineTo(-s4, s2)
            lineTo(size, -s2)
            closePath()
        }

        fun arrowLeft(x: Float, y: Float, size: Float) = SvgPathBuilder().apply {
            val s2 = size / 2f
            val s4 = size / 4f
            moveAbs(x - size / 2, y)
            lineTo(size, -s2)
            lineTo(-s4, s2)
            lineTo(s4, s2)
            lineTo(-size, -s2)
            closePath()
        }

        fun arrowUp(x: Float, y: Float, size: Float) = SvgPathBuilder().apply {
            val s2 = size / 2f
            val s4 = size / 4f
            moveAbs(x, y - size / 2)
            lineTo(-s2, size)
            lineTo(s2, -s4)
            lineTo(s2, s4)
            lineTo(-s2, -size)
            closePath()
        }

        fun arrowDown(x: Float, y: Float, size: Float) = SvgPathBuilder().apply {
            val s2 = size / 2f
            val s4 = size / 4f
            moveAbs(x, y + size / 2)
            lineTo(-s2, -size)
            lineTo(s2, s4)
            lineTo(s2, -s4)
            lineTo(-s2, size)
            closePath()
        }

    }
}