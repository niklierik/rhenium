package me.eriknikli.rhenium.ast.tree.statements

import me.eriknikli.rhenium.ast.tree.expressions.Expression
import me.eriknikli.rhenium.semanticContext.tree.statements.StatementContext
import me.eriknikli.rhenium.semanticContext.tree.statements.WhileStatementContext
import org.antlr.v4.runtime.ParserRuleContext

data class WhileStatement(
    override val parserContext: ParserRuleContext,
    val condition: Expression,
    val body: BlockStatement
) : Statement {
    override val context: StatementContext = WhileStatementContext()
}
