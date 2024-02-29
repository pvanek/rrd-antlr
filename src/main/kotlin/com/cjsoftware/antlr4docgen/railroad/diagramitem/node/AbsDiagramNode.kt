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
import com.cjsoftware.antlr4docgen.railroad.diagramitem.AbsDiagramItem
import com.cjsoftware.antlr4docgen.railroad.diagramitem.container.Alignment

internal abstract class AbsDiagramNode(alignment: Alignment, val caption: String, styleClass: String) :
    AbsDiagramItem(alignment, styleClass) {

    override fun calcHeight(): Float = DiagramSettings.itemHeight + 2f * DiagramSettings.pathWidth

}