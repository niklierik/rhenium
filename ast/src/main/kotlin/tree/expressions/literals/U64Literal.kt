package me.eriknikli.rhenium.ast.tree.expressions.literals

import org.antlr.v4.runtime.ParserRuleContext

data class U64Literal(
    override val parserContext: ParserRuleContext,
    override val value: ULong,
    override val textVersion: String
) : LiteralBase<ULong>()
