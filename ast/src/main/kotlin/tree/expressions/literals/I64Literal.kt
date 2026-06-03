package me.eriknikli.rhenium.ast.tree.expressions.literals

import org.antlr.v4.runtime.ParserRuleContext

data class I64Literal(
    override val parserContext: ParserRuleContext,
    override val value: Long,
    override val textVersion: String
) : LiteralBase<Long>()
