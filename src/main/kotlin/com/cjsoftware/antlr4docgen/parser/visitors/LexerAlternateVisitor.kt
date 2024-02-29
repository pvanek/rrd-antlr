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

class LexerAlternateVisitor : ANTLRv4ParserBaseVisitor<ElementSequence>() {

    override fun visitLexerAlt(ctx: ANTLRv4Parser.LexerAltContext): ElementSequence {
        val elemSeq = mutableListOf<Element>()
        ctx.lexerElements().lexerElement().forEach {
            if (it.actionBlock() == null) {
                val cardinality = it.ebnfSuffix()?.let {
                    if (it.STAR() != null) {
                        Cardinality.ZERO_OR_MORE
                    } else if (it.PLUS() != null) {
                        Cardinality.ONE_OR_MORE
                    } else if (it.QUESTION().size > 0) {
                        Cardinality.ZERO_OR_ONE
                    } else Cardinality.UNSPECIFIED
                } ?: Cardinality.UNSPECIFIED


                elemSeq.add(
                    Element(cardinality,
                        if (it.labeledLexerElement() != null) {
                            it.labeledLexerElement().let {
                                if (it.lexerBlock() != null) {
                                    handleLexerBlock(it.lexerBlock())
                                } else if (it.lexerAtom() != null) {
                                    handleLexerAtom(it.lexerAtom())
                                } else throw IllegalArgumentException("Unknown lexer alt ${ctx.text}")
                            }
                        } else if (it.lexerBlock() != null) {
                            handleLexerBlock(it.lexerBlock())
                        } else if (it.lexerAtom() != null) {
                            handleLexerAtom(it.lexerAtom())
                        } else throw IllegalArgumentException("Unknown lexer alt ${ctx.text}")
                    )
                )
            }
        }
        return ElementSequence(elemSeq)
    }

    private fun handleLexerBlock(ctx: ANTLRv4Parser.LexerBlockContext): Block {
        val altList = mutableListOf<ElementSequence>()
        ctx.lexerAltList().lexerAlt().forEach {
            altList.add(visitLexerAlt(it))
        }
        return Block(altList.toList())
    }


    private fun handleLexerAtom(ctx: ANTLRv4Parser.LexerAtomContext): Atom =
        if (ctx.terminal() != null) {
            if (ctx.terminal().STRING_LITERAL() != null) {
                Literal(ctx.terminal().STRING_LITERAL().text)
            } else {
                RuleRef(ctx.terminal().TOKEN_REF().text)
            }
        } else if (ctx.characterRange() != null) {
            CharacterRange(
                ctx.characterRange().STRING_LITERAL(0).text,
                ctx.characterRange().STRING_LITERAL(1).text
            )
        } else if (ctx.LEXER_CHAR_SET() != null) {
            CharSet(ctx.LEXER_CHAR_SET().text)
        } else if (ctx.notSet() != null) {
            handleExclusionSet(ctx.notSet())
        } else if (ctx.DOT() != null) {
            AnyChar()
        } else throw IllegalArgumentException("Unknown atom: ${ctx.text}")

    private fun handleExclusionSet(ctx: ANTLRv4Parser.NotSetContext): ExclusionSet {
        val exclusions = mutableListOf<Atom>()
        if (ctx.blockSet() != null) {
            ctx.blockSet().setElement().forEach {
                exclusions.add(handleExclusionSetElement(it))
            }
        } else {
            exclusions.add(handleExclusionSetElement(ctx.setElement()))
        }

        return ExclusionSet(exclusions.toList())
    }

    private fun handleExclusionSetElement(ctx: ANTLRv4Parser.SetElementContext): Atom =
        if (ctx.STRING_LITERAL() != null) {
            Literal(ctx.STRING_LITERAL().text)
        } else if (ctx.LEXER_CHAR_SET() != null) {
            CharSet(ctx.LEXER_CHAR_SET().text)
        } else if (ctx.TOKEN_REF() != null) {
            RuleRef(ctx.TOKEN_REF().text)
        } else if (ctx.characterRange() != null) {
            ctx.characterRange().let {
                CharacterRange(it.STRING_LITERAL(0).text, it.STRING_LITERAL(1).text)
            }
        } else throw IllegalArgumentException("Unknown exclusion ${ctx.text}")
}