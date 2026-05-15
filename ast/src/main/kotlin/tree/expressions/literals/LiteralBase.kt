package me.eriknikli.rhenium.ast.tree.expressions.literals

import me.eriknikli.rhenium.semanticContext.tree.expressions.LiteralExpressionContext

abstract class LiteralBase<T> : Literal<T> {
    override val context = LiteralExpressionContext()
}