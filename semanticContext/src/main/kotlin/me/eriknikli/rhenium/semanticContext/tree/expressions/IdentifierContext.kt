package me.eriknikli.rhenium.semanticContext.tree.expressions

import me.eriknikli.rhenium.semanticContext.scope.LeftValueSymbol
import me.eriknikli.rhenium.semanticContext.scope.Scope
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType

class IdentifierContext : LeftValueContext {
    override lateinit var relevantScope: Scope
    override lateinit var symbol: LeftValueSymbol
    override var type: ExpressionType
        get() = symbol.declaredType
        set(_) = throw Exception()
}