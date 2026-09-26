package me.eriknikli.rhenium.ast.tree.statements

import me.eriknikli.rhenium.semanticContext.tree.statements.BlockStatementContext
import me.eriknikli.rhenium.semanticContext.tree.statements.StatementContext
import org.antlr.v4.runtime.ParserRuleContext

data class BlockStatement(
    override val parserContext: ParserRuleContext,
    val statements: List<Statement>
) : Statement {
    override val context: StatementContext = BlockStatementContext()
}
