package me.eriknikli.rhenium.ast.tree.expressions.literals

import org.antlr.v4.runtime.ParserRuleContext

data class F32Literal(
    override val parserContext: ParserRuleContext,
    override val value: Float,
    override val textVersion: String
) : LiteralBase<Float>()
