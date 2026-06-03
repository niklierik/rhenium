package me.eriknikli.rhenium.semanticContext.tree.statements

import me.eriknikli.rhenium.semanticContext.scope.Scope
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import me.eriknikli.rhenium.semanticContext.scope.vars.Variable

class VarDeclarationStatementContext : StatementContext {
    override lateinit var relevantScope: Scope

    lateinit var typeToDeclare: ExpressionType
    lateinit var symbolInfo: Variable
}