package me.eriknikli.rhenium.semanticAnalyzer.diagnostics

import me.eriknikli.rhenium.ast.tree.expressions.operators.Operator
import me.eriknikli.rhenium.common.diagnostics.ContextDiagnostic
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import org.antlr.v4.runtime.ParserRuleContext

data class BinaryOperatorTypeMismatch(
    override val parserContext: ParserRuleContext,
    val actualLeftType: ExpressionType,
    val actualRightType: ExpressionType,
    val operator: Operator
) : ContextDiagnostic {
    override val message: String =
        "operator '${operator.cString}' cannot be applied to $actualLeftType and $actualRightType."
}
