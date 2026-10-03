package me.eriknikli.rhenium.semanticContext.tree.statements

import me.eriknikli.rhenium.semanticContext.scope.Scope
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType

class IncrementStatementContext : StatementContext {
    override lateinit var relevantScope: Scope
    lateinit var type: ExpressionType
}
