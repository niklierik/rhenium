package me.eriknikli.rhenium.ast.tree.statements.vars

import me.eriknikli.rhenium.ast.tree.expressions.Expression
import me.eriknikli.rhenium.ast.tree.expressions.LeftValue
import me.eriknikli.rhenium.ast.tree.expressions.operators.Operator
import me.eriknikli.rhenium.ast.tree.statements.Statement
import me.eriknikli.rhenium.semanticContext.tree.statements.CompoundAssignmentStatementContext
import org.antlr.v4.runtime.ParserRuleContext

data class CompoundAssignmentStatement(
    override val parserContext: ParserRuleContext,
    val leftValue: LeftValue,
    val operator: Operator,
    val rightValue: Expression
) : Statement {
    override val context: CompoundAssignmentStatementContext = CompoundAssignmentStatementContext()
}
