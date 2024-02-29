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

package com.cjsoftware.antlr4docgen.railroad

import com.cjsoftware.antlr4docgen.model.*
import com.cjsoftware.antlr4docgen.railroad.diagramitem.AbsDiagramItem
import com.cjsoftware.antlr4docgen.railroad.diagramitem.container.*
import com.cjsoftware.antlr4docgen.railroad.diagramitem.node.*

private val defined = mutableSetOf<String>()
private val ruleQueue = mutableListOf<Rule>()

internal fun buildDiagram(grammar: List<Rule>): List<Pair<Rule, Sequence>> {
    defined.clear()
    ruleQueue.clear()

    val ruleDiagramList = mutableListOf<Pair<Rule, Sequence>>()

    (grammar.filter { it.referencedBy.isEmpty() }).sortedBy { it.ruleType }.forEach { queueRule(it) }

    while (ruleQueue.isNotEmpty()) {
        ruleQueue.removeLast().let { nextRule ->
            ruleDiagramList.add(
                Pair(nextRule,
                    Sequence(Alignment.START).also {
                        it.add(RuleStart(nextRule.name))
                        it.add(renderAlternatives(nextRule.alternatives, grammar))
                        it.add(RuleEnd())
                    })
            )
        }
    }
    return ruleDiagramList.toList()
}

private fun queueRule(rule: Rule) {
    if (!defined.contains(rule.name)) {
        defined.add(rule.name)
        ruleQueue.add(rule)
    }
}

private fun renderAlternatives(
    alts: List<ElementSequence>,
    grammar: List<Rule>,
): AbsDiagramItem {
    return if (alts.size > 1) {
        Alternatives().apply {
            alts.forEach {
                add(renderSequence(it.elements, grammar))
            }
        }
    } else {
        renderSequence(alts[0].elements, grammar)
    }

}

private fun renderSequence(elems: List<Element>, grammar: List<Rule>): AbsDiagramItem {
    return Sequence().apply {
        elems.forEach { elem ->
            add(renderElement(elem, grammar))
        }
    }
}


internal fun renderElement(element: Element, grammar: List<Rule>): AbsDiagramItem {
    var item = renderAtom(element.atom, grammar)

    if (element.cardinality.repeatable) {
        item = OneOrMore(item)
    }
    if (element.cardinality.optional) {
        item = Optional(item)
    }
    return item
}

internal fun renderAtom(atom: Atom, grammar: List<Rule>): AbsDiagramItem {
    return when (atom) {
        is RuleRef -> renderRuleRef(atom, grammar)
        is Literal -> LexicalLiteral(formatLexicalLiteral(atom.text))
        is AnyChar -> LexicalLiteral("<ANY CHAR>")
        is CharSet -> LexicalLiteral(formatCharSet(atom.chars))
        is CharacterRange -> LexicalLiteral("${atom.start} .. ${atom.end}")
        is AnyToken -> ParserRule("<ANY TOKEN>")
        is Block -> renderAlternatives(atom.alternatives, grammar)
        is ExclusionSet -> renderExclusionSet(atom, grammar)
        else -> LexicalLiteral(atom.toString())
    }
}

private fun formatLexicalLiteral(literal: String) =
    literal.removePrefix("'").removeSuffix("'")

private fun formatCharSet(set: String): String {
    return set
}


internal fun renderRuleRef(atom: RuleRef, grammar: List<Rule>): AbsDiagramItem {
    return grammar.firstOrNull { it.name == atom.ruleName }?.let {
        if (it.ruleType == RuleType.LEXICAL) {
            if (it.alternatives.size == 1 && it.alternatives[0].elements.size == 1) {
                renderElement(it.alternatives[0].elements[0], grammar)
            } else {
                queueRule(it)
                LexicalRule(atom.ruleName)
            }
        } else {
            queueRule(it)
            ParserRule(atom.ruleName)
        }
    } ?: LexicalLiteral("RULE NOT FOUND: ${atom.ruleName}")
}

internal fun renderExclusionSet(atom: ExclusionSet, grammar: List<Rule>): AbsDiagramItem {
    return Sequence().apply {
        add(LexicalLiteral("Any Char except:"))
        atom.exclusionList.forEach {
            add(renderAtom(it, grammar))
        }
    }
}
