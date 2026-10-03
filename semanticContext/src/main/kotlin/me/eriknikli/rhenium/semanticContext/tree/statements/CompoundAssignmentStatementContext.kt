package me.eriknikli.rhenium.semanticContext.tree.statements

import me.eriknikli.rhenium.semanticContext.scope.Scope
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType

class CompoundAssignmentStatementContext : StatementContext {
    override lateinit var relevantScope: Scope
    lateinit var type: ExpressionType
}
