package me.eriknikli.rhenium.semanticContext.tree.statements

import me.eriknikli.rhenium.semanticContext.scope.Scope

class WhileStatementContext : StatementContext {
    override lateinit var relevantScope: Scope
}
