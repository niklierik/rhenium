package me.eriknikli.rhenium.semanticAnalyzer.exceptions

import me.eriknikli.rhenium.ast.tree.expressions.operators.Operator
import me.eriknikli.rhenium.common.location
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import org.antlr.v4.runtime.ParserRuleContext

class IllegalBinaryOperation(
    val parserContext: ParserRuleContext,
    val left: ExpressionType,
    val right: ExpressionType,
    val operator: Operator
) : SemanticException(
    "${parserContext.location}: illegal binary operation '$left ${operator.cString} $right'."
)
