package me.eriknikli.rhenium.semanticAnalyzer.exceptions

import me.eriknikli.rhenium.ast.tree.expressions.operators.Operator
import me.eriknikli.rhenium.common.location
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import org.antlr.v4.runtime.ParserRuleContext

class IllegalUnaryOperation(
    val parserContext: ParserRuleContext,
    val expression: ExpressionType,
    val operator: Operator
) : SemanticException(
    "${parserContext.location}: illegal unary operation '${operator.cString}$expression'."
)
