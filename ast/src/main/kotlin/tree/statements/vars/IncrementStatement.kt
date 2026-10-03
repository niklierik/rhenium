package me.eriknikli.rhenium.ast.tree.statements.vars

import me.eriknikli.rhenium.ast.tree.expressions.LeftValue
import me.eriknikli.rhenium.ast.tree.expressions.operators.Operator
import me.eriknikli.rhenium.ast.tree.statements.Statement
import me.eriknikli.rhenium.semanticContext.tree.statements.IncrementStatementContext
import org.antlr.v4.runtime.ParserRuleContext

data class IncrementStatement(
    override val parserContext: ParserRuleContext,
    val leftValue: LeftValue,
    val operator: Operator
) : Statement {
    override val context: IncrementStatementContext = IncrementStatementContext()
}
