package me.eriknikli.rhenium.ast.tree.expressions.literals

import org.antlr.v4.runtime.ParserRuleContext

data class U8Literal(
    override val parserContext: ParserRuleContext,
    override val value: UByte,
    override val textVersion: String
) : LiteralBase<UByte>()
