package me.eriknikli.rhenium.ast.tree.expressions.literals

import org.antlr.v4.runtime.ParserRuleContext

data class I32Literal(
    override val parserContext: ParserRuleContext,
    override val value: Int,
    override val textVersion: String
) : LiteralBase<Int>()
