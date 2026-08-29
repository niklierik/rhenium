package me.eriknikli.rhenium.semanticAnalyzer.diagnostics

import me.eriknikli.rhenium.ast.tree.expressions.operators.Operator
import me.eriknikli.rhenium.common.diagnostics.ContextDiagnostic
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import org.antlr.v4.runtime.ParserRuleContext

data class IllegalUnaryOperation(
    override val parserContext: ParserRuleContext,
    val expression: ExpressionType,
    val operator: Operator
) : ContextDiagnostic {
    override val message: String = "illegal unary operation '${operator.cString}$expression'."
}
