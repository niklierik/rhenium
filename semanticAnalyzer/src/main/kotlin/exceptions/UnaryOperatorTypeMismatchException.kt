package me.eriknikli.rhenium.semanticAnalyzer.exceptions

import me.eriknikli.rhenium.ast.tree.expressions.operators.Operator
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import org.antlr.v4.runtime.ParserRuleContext

class UnaryOperatorTypeMismatchException(
    val parserRuleContext: ParserRuleContext,
    val operator: Operator,
    val actualType: ExpressionType,
    vararg expectedType: ExpressionType
) : SemanticException() {
}