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

package com.cjsoftware.antlr4docgen.parser.visitors

import com.cjsoftware.antlr4docgen.model.*
import com.cjsoftware.antlr4docgen.parser.ANTLRv4Parser
import com.cjsoftware.antlr4docgen.parser.ANTLRv4ParserBaseVisitor

class AlternativeVisitor : ANTLRv4ParserBaseVisitor<ElementSequence>() {

    override fun visitAlternative(ctx: ANTLRv4Parser.AlternativeContext): ElementSequence {
        val elementList = mutableListOf<Element>()
        ctx.element().forEach {
            if (it.actionBlock() == null) {
                elementList.add(
                    if (it.labeledElement() != null) {
                        val ebnfQualifier = handleEbnf(it.ebnfSuffix())
                        it.labeledElement().let {
                            if (it.atom() != null) {
                                Element(ebnfQualifier, handleParserAtom(it.atom()))
                            } else if (it.block() != null) {
                                Element(ebnfQualifier, handleParserBlock(it.block()))
                            } else throw IllegalArgumentException("Unrecognised alternative : ${ctx.text}")
                        }
                    } else if (it.atom() != null) {
                        val ebnfQualifier = handleEbnf(it.ebnfSuffix())
                        Element(ebnfQualifier, handleParserAtom(it.atom()))
                    } else if (it.ebnf() != null) {
                        it.ebnf().let {
                            val ebnfQualifier = handleEbnf(it.blockSuffix()?.ebnfSuffix())
                            Element(ebnfQualifier, handleParserBlock(it.block()))
                        }
                    } else throw IllegalArgumentException("Unrecognised alternative : ${ctx.text}")
                )
            }
        }

        return ElementSequence(elementList.toList())
    }

    private fun handleParserBlock(ctx: ANTLRv4Parser.BlockContext): Block {
        val altList = mutableListOf<ElementSequence>()
        ctx.altList().alternative().forEach {
            altList.add(visitAlternative(it))
        }
        return Block(altList.toList())
    }

    private fun handleParserAtom(ctx: ANTLRv4Parser.AtomContext): Atom =
        if (ctx.DOT() != null) {
            AnyToken()
        } else if (ctx.ruleref() != null) {
            RuleRef(ctx.ruleref().text)
        } else if (ctx.notSet() != null) {
            ExclusionSet(emptyList())
        } else if (ctx.terminal() != null) {
            ctx.terminal().let {
                if (it.STRING_LITERAL() != null) {
                    Literal(it.STRING_LITERAL().text)
                } else {
                    RuleRef(it.TOKEN_REF().text)
                }
            }
        } else throw IllegalArgumentException("Unknown parser atom : ${ctx.text}")


    private fun handleEbnf(ctx: ANTLRv4Parser.EbnfSuffixContext?): Cardinality =
        ctx?.let {
            if (it.STAR() != null) {
                Cardinality.ZERO_OR_MORE
            } else if (it.PLUS() != null) {
                Cardinality.ONE_OR_MORE
            } else if (it.QUESTION().size > 0) {
                Cardinality.ZERO_OR_ONE
            } else Cardinality.UNSPECIFIED
        } ?: Cardinality.UNSPECIFIED
}