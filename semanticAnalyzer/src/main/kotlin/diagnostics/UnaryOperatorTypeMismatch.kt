package me.eriknikli.rhenium.semanticAnalyzer.diagnostics

import me.eriknikli.rhenium.ast.tree.expressions.operators.Operator
import me.eriknikli.rhenium.common.diagnostics.ContextDiagnostic
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import org.antlr.v4.runtime.ParserRuleContext

data class UnaryOperatorTypeMismatch(
    override val parserContext: ParserRuleContext,
    val operator: Operator,
    val actualType: ExpressionType,
    val expectedTypes: List<ExpressionType>
) : ContextDiagnostic {
    override val message: String =
        "operator '${operator.cString}' cannot be applied to $actualType, expected " +
                "${expectedTypes.joinToString(" or ")}."
}
