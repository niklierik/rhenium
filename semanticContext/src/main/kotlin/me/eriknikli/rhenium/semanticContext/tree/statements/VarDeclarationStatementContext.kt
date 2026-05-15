package me.eriknikli.rhenium.semanticContext.tree.statements

import me.eriknikli.rhenium.semanticContext.types.ExpressionType

class VarDeclarationStatementContext : StatementContext {
    lateinit var typeToDeclare: ExpressionType
}