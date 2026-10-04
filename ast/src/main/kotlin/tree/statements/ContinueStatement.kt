package me.eriknikli.rhenium.ast.tree.statements

import me.eriknikli.rhenium.semanticContext.tree.statements.ContinueStatementContext
import me.eriknikli.rhenium.semanticContext.tree.statements.StatementContext
import org.antlr.v4.runtime.ParserRuleContext

data class ContinueStatement(
    override val parserContext: ParserRuleContext
) : LoopControlStatement {
    override val context: StatementContext = ContinueStatementContext()
    override val keyword: String = "continue"
}
