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

import com.cjsoftware.antlr4docgen.railroad.diagramitem.AbsDiagramItem

internal typealias DiagramItemValueSelector = (AbsDiagramItem) -> Float

enum class Alignment {
    START,
    END,
    MIDDLE
}

abstract class AbsDiagramContainer(alignment: Alignment, styleClass: String) :
    AbsDiagramItem(alignment, styleClass) {

    val children = mutableListOf<AbsDiagramItem>()

    fun maxOfChildren(selector: DiagramItemValueSelector): Float {
        var maxVal = 0f
        children.forEach { maxVal = java.lang.Float.max(maxVal, selector(it)) }
        return maxVal
    }

    fun sumOfChildren(selector: DiagramItemValueSelector): Float {
        var sumVal = 0f
        children.forEach { sumVal += selector(it) }
        return sumVal
    }

    fun add(item: AbsDiagramItem) = this.apply {
        children.add(item)
    }
}

