package me.eriknikli.rhenium.ast.tree.statements.vars

import me.eriknikli.rhenium.ast.tree.expressions.Expression
import me.eriknikli.rhenium.ast.tree.expressions.LeftValue
import me.eriknikli.rhenium.ast.tree.statements.Statement
import me.eriknikli.rhenium.semanticContext.tree.statements.VarAssignmentStatementContext
import org.antlr.v4.runtime.ParserRuleContext

data class VarAssignmentStatement(
    override val parserContext: ParserRuleContext,
    val leftValue: LeftValue,
    val rightValue: Expression
) : Statement {
    override val context: VarAssignmentStatementContext = VarAssignmentStatementContext()
}