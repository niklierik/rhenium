package me.eriknikli.rhenium.ast.tree.expressions.literals

import org.antlr.v4.runtime.ParserRuleContext

data class I16Literal(
    override val parserContext: ParserRuleContext,
    override val value: Short,
    override val textVersion: String
) : LiteralBase<Short>()
