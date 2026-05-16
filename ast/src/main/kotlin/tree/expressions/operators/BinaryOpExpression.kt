package me.eriknikli.rhenium.ast.tree.expressions.operators

import me.eriknikli.rhenium.ast.tree.expressions.Expression
import me.eriknikli.rhenium.semanticContext.tree.expressions.BinaryOpContext
import me.eriknikli.rhenium.semanticContext.tree.expressions.ExpressionContext

data class BinaryOpExpression(
    val left: Expression,
    val operator: Operator,
    val right: Expression
) : Expression {
    override val context: ExpressionContext = BinaryOpContext()
}
