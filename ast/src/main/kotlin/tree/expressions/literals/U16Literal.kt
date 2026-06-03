package me.eriknikli.rhenium.ast.tree.expressions.literals

import org.antlr.v4.runtime.ParserRuleContext

data class U16Literal(
    override val parserContext: ParserRuleContext,
    override val value: UShort,
    override val textVersion: String
) : LiteralBase<UShort>()
