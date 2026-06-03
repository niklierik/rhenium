package me.eriknikli.rhenium.ast.tree.expressions.literals

import org.antlr.v4.runtime.ParserRuleContext

data class F64Literal(
    override val parserContext: ParserRuleContext,
    override val value: Double,
    override val textVersion: String
) : LiteralBase<Double>()
