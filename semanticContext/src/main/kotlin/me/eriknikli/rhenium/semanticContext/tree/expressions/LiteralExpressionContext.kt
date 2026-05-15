package me.eriknikli.rhenium.semanticContext.tree.expressions

import me.eriknikli.rhenium.semanticContext.types.ExpressionType

class LiteralExpressionContext : ExpressionContext {
    override lateinit var type: ExpressionType
}