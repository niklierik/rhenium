package me.eriknikli.rhenium.semanticAnalyzer.diagnostics

import me.eriknikli.rhenium.ast.tree.expressions.operators.WrittenOperator
import me.eriknikli.rhenium.common.diagnostics.ContextDiagnostic
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import org.antlr.v4.runtime.ParserRuleContext

data class IllegalIncrement(
    override val parserContext: ParserRuleContext,
    val type: ExpressionType,
    val operator: WrittenOperator.Increment
) : ContextDiagnostic {
    override val message: String = "illegal ${operator.step.word} '$type${operator.spelling}'."
}
