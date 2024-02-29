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
import com.cjsoftware.antlr4docgen.railroad.diagramitem.container.Alignment

abstract class AbsDiagramItem(val alignment: Alignment, val styleClass: String) {
    abstract fun calcWidth(): Float
    abstract fun calcHeight(): Float
    open fun entryLineOffsetY() = 0f
    open fun entryLineY(offset: Float) = offset + DiagramSettings.halfItemHeight + DiagramSettings.halfPathWidth

    abstract fun renderAt(x: Float, y: Float, containerWidth: Float): String

    // region Helpers
    fun startSpace(alignment: Alignment, availableSize: Float, actualSize: Float) =
        when (alignment) {
            Alignment.START -> 0f
            Alignment.END -> availableSize - actualSize
            Alignment.MIDDLE -> (availableSize - actualSize) / 2
        }


    fun htmlEscape(raw: String) =
        raw.replace("&", "&amp;").replace("'", "&apos;")
            .replace("\"", "&quot;").replace("<", "&lt;")
            .replace(">", "&gt;")

    fun quotVal(v: Any) = "\"$v\""
    // endregion

    // region svg html helpers
    fun svgAnchor(url: String, content: String) =
        "<a href=${quotVal(url)}>$content</a>"

    fun svgLine(x1: Float, y1: Float, x2: Float, y2: Float, svgClass: String = styleClass) =
        StringBuilder().apply {
            append("<line ")
            append("x1=${quotVal(x1)} y1=${quotVal(y1)} ")
            append("x2=${quotVal(x2)} y2=${quotVal(y2)} ")
            if (svgClass.isNotBlank()) append("class=${quotVal(svgClass)} ")
            else append("/>")

        }.toString()

    fun svgCircle(x: Float, y: Float, r: Float, svgClass: String = styleClass) =
        StringBuilder().apply {
            append("<circle ")
            append("cx=${quotVal(x)} cy=${quotVal(y)} ")
            append("r=${quotVal(r)} ")
            if (svgClass.isNotBlank()) append("class=${quotVal(svgClass)} ")
            append("/>")
        }.toString()

    fun svgRectangle(x: Float, y: Float, w: Float, h: Float, svgClass: String = styleClass) =
        StringBuilder().apply {
            append("<rect ")
            append("x=${quotVal(x)} y=${quotVal(y)} ")
            append("width=${quotVal(w)} height=${quotVal(h)} ")
            if (svgClass.isNotBlank()) append("class=${quotVal(svgClass)} ")
            append("/>")
        }.toString()

    fun svgCapsule(x: Float, y: Float, w: Float, h: Float, svgClass: String = styleClass) =
        StringBuilder().apply {
            append("<rect ")
            append("x=${quotVal(x)} y=${quotVal(y)} ")
            append("width=${quotVal(w)} height=${quotVal(h)} ")
            append("rx=${quotVal((h / 2f) - 1f)} ")
            if (svgClass.isNotBlank() == true) append("class=${quotVal(svgClass)} ")
            append("/>")
        }.toString()

    fun svgText(x: Float, y: Float, text: String, svgClass: String = styleClass) =
        StringBuilder().apply {
            append("<text x=${quotVal(x)} y=${quotVal(y)} alignment-baseline=${quotVal("Middle")} ")
            if (svgClass.isNotBlank() == true) append("class=${quotVal(svgClass)} ")
            append(">")
            append(htmlEscape(text))
            append("</text>")
        }.toString()

    // endregion
}