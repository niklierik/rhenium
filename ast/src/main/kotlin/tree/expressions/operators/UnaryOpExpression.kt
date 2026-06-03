package me.eriknikli.rhenium.ast.tree.expressions.operators

import me.eriknikli.rhenium.ast.tree.expressions.Expression
import me.eriknikli.rhenium.semanticContext.tree.expressions.UnaryOpContext
import org.antlr.v4.runtime.ParserRuleContext

data class UnaryOpExpression(
    override val parserContext: ParserRuleContext,
    val operator: Operator,
    val expression: Expression
) : Expression {
    override val context = UnaryOpContext()
}