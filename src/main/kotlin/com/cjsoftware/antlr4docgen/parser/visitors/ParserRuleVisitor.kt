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

class ParserRuleVisitor : ANTLRv4ParserBaseVisitor<Rule>() {

    override fun visitParserRuleSpec(ctx: ANTLRv4Parser.ParserRuleSpecContext): Rule {

        val ruleName = ctx.RULE_REF().text
        return Rule(
            ruleName, RuleType.SYNTAX,
            buildRuleAlternativelist(ctx.ruleBlock().ruleAltList())
        )
    }

    private fun buildRuleAlternativelist(ctx: ANTLRv4Parser.RuleAltListContext): List<ElementSequence> {
        val ruleAlts = mutableListOf<ElementSequence>()
        ctx.labeledAlt().forEach {
            ruleAlts.add(it.alternative().accept(AlternativeVisitor()))
        }

        return ruleAlts.toList()
    }
}