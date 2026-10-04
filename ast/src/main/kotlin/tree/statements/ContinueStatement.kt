package me.eriknikli.rhenium.ast.tree.statements

import me.eriknikli.rhenium.semanticContext.tree.statements.ContinueStatementContext
import me.eriknikli.rhenium.semanticContext.tree.statements.StatementContext
import org.antlr.v4.runtime.ParserRuleContext

data class ContinueStatement(
    override val parserContext: ParserRuleContext
) : Statement {
    override val context: StatementContext = ContinueStatementContext()
}
