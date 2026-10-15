package me.eriknikli.rhenium.ast.tree.statements

import me.eriknikli.rhenium.ast.tree.expressions.Expression
import me.eriknikli.rhenium.semanticContext.tree.statements.ForStatementContext
import me.eriknikli.rhenium.semanticContext.tree.statements.StatementContext
import org.antlr.v4.runtime.ParserRuleContext

data class ForStatement(
    override val parserContext: ParserRuleContext,
    val initClause: Statement,
    val condition: Expression,
    val iterationExpression: Statement,
    val body: BlockStatement
) : Statement {
    override val context: StatementContext = ForStatementContext()
}
