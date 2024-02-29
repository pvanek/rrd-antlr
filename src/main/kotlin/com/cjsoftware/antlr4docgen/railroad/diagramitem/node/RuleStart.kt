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
import com.cjsoftware.antlr4docgen.railroad.diagramitem.SvgPathBuilder
import com.cjsoftware.antlr4docgen.railroad.diagramitem.container.Alignment

internal class RuleStart(ruleName: String, alignment: Alignment = Alignment.MIDDLE) :
    AbsDiagramNode(alignment, ruleName, DiagramSettings.STYLE_RULE_START) {

    override fun calcWidth(): Float {
        val labelSize = java.lang.Float.max(
            caption.length * (DiagramSettings.ruleStartCharSize),
            DiagramSettings.itemHeight
        ) + (2 * DiagramSettings.innerPadding) + (2 * DiagramSettings.pathWidth)
        return labelSize + 2 * DiagramSettings.startSymSize
    }

    override fun renderAt(x: Float, y: Float, containerWidth: Float) = StringBuilder().apply {

        val myWidth = calcWidth()
        val spaceStart = startSpace(alignment, containerWidth, myWidth)
        val capsuleSize = myWidth - 2 * DiagramSettings.startSymSize
        append(
            renderCapsuleContainer(
                x + spaceStart + DiagramSettings.halfPathWidth,
                y + DiagramSettings.halfPathWidth,
                capsuleSize,
                DiagramSettings.itemHeight
            )
        )
        append(
            SvgPathBuilder.arrowRight(
                x + spaceStart + capsuleSize + DiagramSettings.startSymSize * 2, y + entryLineY(entryLineOffsetY()),
                DiagramSettings.startSymSize
            ).build(DiagramSettings.STYLE_PATH_SYMBOL)
        )

        append(
            svgLine(
                x + spaceStart + capsuleSize,
                y + entryLineY(entryLineOffsetY()),
                x + containerWidth,
                y + entryLineY(entryLineOffsetY()), ""
            )
        )


    }.toString()


    private fun renderCapsuleContainer(x: Float, y: Float, width: Float, height: Float) = StringBuilder().apply {
        append(svgCapsule(x, y, width, height, styleClass))
        append(svgText(x + width / 2, y + height / 2, caption, styleClass))
    }.toString()
}