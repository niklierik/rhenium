package me.eriknikli.rhenium.ast.tree.expressions.literals

import me.eriknikli.rhenium.ast.tree.expressions.Expression
import me.eriknikli.rhenium.semanticContext.tree.expressions.LiteralExpressionContext

interface Literal<T> : Expression {
    val value: T
    val textVersion: String
    override val context: LiteralExpressionContext
}