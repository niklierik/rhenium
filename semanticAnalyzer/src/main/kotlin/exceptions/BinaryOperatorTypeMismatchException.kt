package me.eriknikli.rhenium.semanticAnalyzer.exceptions

import me.eriknikli.rhenium.ast.tree.expressions.operators.Operator
import me.eriknikli.rhenium.common.location
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import org.antlr.v4.runtime.ParserRuleContext

class BinaryOperatorTypeMismatchException(
    val parserRuleContext: ParserRuleContext,
    val actualLeftType: ExpressionType,
    val actualRightType: ExpressionType,
    val operator: Operator
) : SemanticException(
    "${parserRuleContext.location}: operator '${operator.cString}' cannot be applied to " +
            "$actualLeftType and $actualRightType."
)
