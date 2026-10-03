package me.eriknikli.rhenium.semanticAnalyzer.diagnostics

import me.eriknikli.rhenium.ast.tree.expressions.operators.Operator
import me.eriknikli.rhenium.common.diagnostics.ContextDiagnostic
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import org.antlr.v4.runtime.ParserRuleContext

data class IllegalIncrement(
    override val parserContext: ParserRuleContext,
    val type: ExpressionType,
    val operator: Operator
) : ContextDiagnostic {
    private val kind = if (operator == Operator.PLUS) "increment" else "decrement"

    override val message: String = "illegal $kind '$type${operator.cString}${operator.cString}'."
}
