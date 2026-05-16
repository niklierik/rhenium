package me.eriknikli.rhenium.semanticContext.tree.expressions

import me.eriknikli.rhenium.semanticContext.scope.Scope
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType

class IdentifierContext : ExpressionContext {
    override lateinit var relevantScope: Scope
    override lateinit var type: ExpressionType
}