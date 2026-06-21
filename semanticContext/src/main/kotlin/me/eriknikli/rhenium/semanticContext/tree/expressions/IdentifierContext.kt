package me.eriknikli.rhenium.semanticContext.tree.expressions

import me.eriknikli.rhenium.semanticContext.scope.Scope
import me.eriknikli.rhenium.semanticContext.scope.Symbol
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType

class IdentifierContext : LValueContext {
    override lateinit var relevantScope: Scope
    override lateinit var type: ExpressionType
    override lateinit var symbol: Symbol
    override var mutable: Boolean = false
}