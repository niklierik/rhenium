package me.eriknikli.rhenium.semanticContext.tree.expressions

import me.eriknikli.rhenium.semanticContext.scope.Scope
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType

class BinaryOpContext : ExpressionContext {
    override lateinit var type: ExpressionType
    override lateinit var relevantScope: Scope
}