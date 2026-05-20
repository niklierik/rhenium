package me.eriknikli.rhenium.ast.tree.expressions.literals

import me.eriknikli.rhenium.semanticContext.tree.expressions.LiteralExpressionContext

data class BooleanLiteral(
    override val value: Boolean,
    override val textVersion: String
) : Literal<Boolean> {
    override val context: LiteralExpressionContext = LiteralExpressionContext()
}
