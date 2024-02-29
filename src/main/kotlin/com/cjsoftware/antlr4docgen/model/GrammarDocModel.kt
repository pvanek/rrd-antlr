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

package com.cjsoftware.antlr4docgen.model

enum class Cardinality(val optional: Boolean, val repeatable: Boolean) {
    UNSPECIFIED(false, false),
    ZERO_OR_ONE(true, false),
    ONE_OR_MORE(false, true),
    ZERO_OR_MORE(true, true),
}

enum class RuleType {
    LEXICAL,
    SYNTAX
}

class Rule(
    val name: String,
    val ruleType: RuleType,
    val alternatives: List<ElementSequence>
) {
    val referencedBy = mutableListOf<Rule>()
    val references = mutableListOf<Rule>()
}

class ElementSequence(val elements: List<Element>)

class Element(val cardinality: Cardinality, val atom: Atom)
abstract class Atom

class Block(val alternatives: List<ElementSequence>) : Atom()

class RuleRef(val ruleName: String) : Atom()

class ExclusionSet(val exclusionList: List<Atom>) : Atom()

class AnyToken : Atom()

class Literal(val text: String) : Atom()


class CharacterRange(val start: String, val end: String) : Atom()

class CharSet(val chars: String) : Atom()

class AnyChar : Atom()