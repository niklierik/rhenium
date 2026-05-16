package me.eriknikli.rhenium.ast.tree.expressions.operators

import me.eriknikli.rhenium.ast.tree.expressions.Expression
import me.eriknikli.rhenium.semanticContext.tree.expressions.UnaryOpContext

data class UnaryOpExpression(
    val operator: Operator,
    val expression: Expression
) : Expression {
    override val context = UnaryOpContext()
}