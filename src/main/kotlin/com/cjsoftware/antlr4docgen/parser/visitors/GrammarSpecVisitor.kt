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

import com.cjsoftware.antlr4docgen.parser.ANTLRv4Parser
import com.cjsoftware.antlr4docgen.parser.ANTLRv4ParserBaseVisitor
import com.cjsoftware.antlr4docgen.parser.GrammarModelBuilder
import org.antlr.v4.runtime.tree.TerminalNode
import java.io.File


class GrammarSpecVisitor(private val grammarModelBuilder: GrammarModelBuilder) : ANTLRv4ParserBaseVisitor<Unit>() {

    override fun visitOption(ctx: ANTLRv4Parser.OptionContext) {

        fun resolveOptionValue(ctx: ANTLRv4Parser.OptionValueContext): String =
            if (ctx.INT() != null) {
                ctx.INT().text
            } else if (ctx.STRING_LITERAL() != null) {
                (ctx.STRING_LITERAL() as TerminalNode).text.removePrefix("'").removeSuffix("'")
            } else {
                ctx.identifier().map { it.TOKEN_REF()?.text }.joinToString(separator = ".")
            }

        if (ctx.identifier().text == "tokenVocab") {
            val tokenFile = resolveOptionValue(ctx.optionValue())
            grammarModelBuilder.processFile(File("$tokenFile.g4"))
        }
    }

    override fun visitDelegateGrammar(ctx: ANTLRv4Parser.DelegateGrammarContext) {
        val identifier = ctx.identifier()[ctx.identifier().size - 1].text
        grammarModelBuilder.processFile(File("$identifier.g4"))
    }

    override fun visitParserRuleSpec(ctx: ANTLRv4Parser.ParserRuleSpecContext) {
        grammarModelBuilder.addRule(ctx.accept(ParserRuleVisitor()))
    }


    override fun visitLexerRuleSpec(ctx: ANTLRv4Parser.LexerRuleSpecContext) {
        grammarModelBuilder.addRule(ctx.accept(LexerRuleVisitor()))

    }

}