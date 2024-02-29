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

package com.cjsoftware.antlr4docgen.parser

import com.cjsoftware.antlr4docgen.model.*
import com.cjsoftware.antlr4docgen.parser.visitors.GrammarSpecVisitor
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

interface ParserStreamProvider {
    fun getStream(name: String): InputStream
}

class DefaultStreamProvider(private val baseFolder: File) : ParserStreamProvider {
    override fun getStream(name: String) = FileInputStream(File(baseFolder, name))
}

open class GrammarModelBuilder(val streamProvider: ParserStreamProvider) {

    val rules = mutableMapOf<String, Rule>()

    private fun getParser(script: InputStream, channel: Int = ANTLRv4Lexer.DEFAULT_TOKEN_CHANNEL) =
        ANTLRv4Parser(
            CommonTokenStream(
                ANTLRv4Lexer(
                    CharStreams.fromStream(script)
                ),
                channel
            )
        )

    private fun processInput(stream: InputStream) {
        getParser(stream).grammarSpec().accept(
            GrammarSpecVisitor(this)
        )
    }

    fun processFile(file: File) {
        streamProvider.getStream(file.name).use {
            processInput(it)
        }
    }

    fun processGrammar(file: File): List<Rule> {
        rules["EOF"] = Rule(
            "EOF",
            RuleType.LEXICAL,
            listOf(ElementSequence(listOf(Element(Cardinality.UNSPECIFIED, Literal("<End of File>")))))
        )
        processFile(file)
        resolveReferences(rules)
        return rules.values.toList()
    }

    fun addRule(rule: Rule) {
        rules[rule.name] = rule
    }

    private fun resolveReferences(rules: MutableMap<String, Rule>) {
        fun processAlts(rule: Rule, alts: List<ElementSequence>) {
            fun processAtom(rule: Rule, atom: Atom) {
                when (atom) {
                    is RuleRef -> {
                        requireNotNull(rules[atom.ruleName]).also {
                            if (!it.referencedBy.contains(rule)) {
                                it.referencedBy.add(rule)
                            }
                            if (!rule.references.contains(it)) {
                                rule.references.add(it)
                            }
                        }
                    }

                    is Block -> {
                        processAlts(rule, atom.alternatives)
                    }

                    is ExclusionSet -> {
                        atom.exclusionList.forEach { processAtom(rule, it) }
                    }
                }
            }

            alts.forEach { ruleAlt ->
                ruleAlt.elements.forEach { element ->
                    processAtom(rule, element.atom)
                }
            }
        }

        rules.values.forEach { rule ->
            processAlts(rule, rule.alternatives)
        }
    }

}

