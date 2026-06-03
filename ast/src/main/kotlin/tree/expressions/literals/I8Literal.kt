package me.eriknikli.rhenium.ast.tree.expressions.literals

import org.antlr.v4.runtime.ParserRuleContext

data class I8Literal(
    override val parserContext: ParserRuleContext,
    override val value: Byte,
    override val textVersion: String
) : LiteralBase<Byte>()
