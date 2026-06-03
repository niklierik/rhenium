package me.eriknikli.rhenium.ast.tree.expressions.literals

import org.antlr.v4.runtime.ParserRuleContext

data class U32Literal(
    override val parserContext: ParserRuleContext,
    override val value: UInt,
    override val textVersion: String
) : LiteralBase<UInt>()
