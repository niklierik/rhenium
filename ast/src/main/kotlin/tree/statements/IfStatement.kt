package me.eriknikli.rhenium.ast.tree.statements

import me.eriknikli.rhenium.ast.tree.expressions.Expression
import me.eriknikli.rhenium.semanticContext.tree.statements.IfStatementContext
import me.eriknikli.rhenium.semanticContext.tree.statements.StatementContext
import org.antlr.v4.runtime.ParserRuleContext

data class IfStatement(
    override val parserContext: ParserRuleContext,
    val condition: Expression,
    val thenBranch: BlockStatement,
    val elseBranch: ElseBranch?
) : ElseBranch {
    override val context: StatementContext = IfStatementContext()
}
