package me.eriknikli.rhenium.ast.tree.statements

import me.eriknikli.rhenium.ast.tree.expressions.Expression
import me.eriknikli.rhenium.semanticContext.tree.statements.ExpressionStatementContext
import me.eriknikli.rhenium.semanticContext.tree.statements.StatementContext

data class ExpressionStatement(
    val expression: Expression
) : Statement {
    override val context: StatementContext = ExpressionStatementContext()
}
