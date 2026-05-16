package me.eriknikli.rhenium.semanticContext.tree.statements

import me.eriknikli.rhenium.semanticContext.scope.Scope

class VarAssignmentStatementContext : StatementContext {
    override lateinit var relevantScope: Scope
}