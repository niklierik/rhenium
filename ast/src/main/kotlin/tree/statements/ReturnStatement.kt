package me.eriknikli.rhenium.ast.tree.statements

import me.eriknikli.rhenium.ast.tree.expressions.Expression
import me.eriknikli.rhenium.semanticContext.tree.statements.ReturnStatementContext
import org.antlr.v4.runtime.ParserRuleContext

data class ReturnStatement(
    override val parserContext: ParserRuleContext,
    val value: Expression?
) : ControlTransferStatement {
    override val context: ReturnStatementContext = ReturnStatementContext()
    override val keyword: String = "return"
}
