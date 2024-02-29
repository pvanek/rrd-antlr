package com.cjsoftware.antlr4docgen.railroad

import com.cjsoftware.antlr4docgen.model.Rule
import com.cjsoftware.antlr4docgen.railroad.diagramitem.container.Sequence
import java.nio.file.Path

interface IDiagram {
    fun render(rootPath: Path, ruleDiagramList: List<Pair<Rule, Sequence>>)
}
