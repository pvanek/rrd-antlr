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

import com.cjsoftware.antlr4docgen.model.ElementSequence
import com.cjsoftware.antlr4docgen.model.Rule
import com.cjsoftware.antlr4docgen.model.RuleType
import com.cjsoftware.antlr4docgen.parser.ANTLRv4Parser
import com.cjsoftware.antlr4docgen.parser.ANTLRv4ParserBaseVisitor

class LexerRuleVisitor : ANTLRv4ParserBaseVisitor<Rule>() {

    override fun visitLexerRuleSpec(ctx: ANTLRv4Parser.LexerRuleSpecContext): Rule {
        val ruleName = ctx.TOKEN_REF().text
        return Rule(
            ruleName, RuleType.LEXICAL,
            buildLexerAltList(ctx.lexerRuleBlock().lexerAltList())
        )
    }

    private fun buildLexerAltList(ctx: ANTLRv4Parser.LexerAltListContext): List<ElementSequence> {
        val altList = mutableListOf<ElementSequence>()
        ctx.lexerAlt().forEach {
            altList.add(it.accept(LexerAlternateVisitor()))
        }
        return altList.toList()
    }

}