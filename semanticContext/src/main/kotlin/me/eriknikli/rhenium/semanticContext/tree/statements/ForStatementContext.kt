package me.eriknikli.rhenium.semanticContext.tree.statements

import me.eriknikli.rhenium.semanticContext.scope.Scope

class ForStatementContext : StatementContext {
    override lateinit var relevantScope: Scope
}
