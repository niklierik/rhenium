package me.eriknikli.rhenium.ast.tree.statements

import me.eriknikli.rhenium.semanticContext.tree.statements.BreakStatementContext
import me.eriknikli.rhenium.semanticContext.tree.statements.StatementContext
import org.antlr.v4.runtime.ParserRuleContext

data class BreakStatement(
    override val parserContext: ParserRuleContext
) : LoopControlStatement {
    override val context: StatementContext = BreakStatementContext()
    override val keyword: String = "break"
}
