package me.eriknikli.rhenium.ast.tree.statements

import me.eriknikli.rhenium.ast.tree.expressions.Expression
import me.eriknikli.rhenium.semanticContext.tree.statements.PrintStatementContext
import me.eriknikli.rhenium.semanticContext.tree.statements.StatementContext
import org.antlr.v4.runtime.ParserRuleContext

data class PrintStatement(
    override val parserContext: ParserRuleContext,
    val newLine: Boolean,
    val expression: Expression?
) : Statement {
    override val context: StatementContext = PrintStatementContext()
}
