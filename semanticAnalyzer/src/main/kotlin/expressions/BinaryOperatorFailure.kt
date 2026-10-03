package me.eriknikli.rhenium.semanticAnalyzer.expressions

import me.eriknikli.rhenium.ast.tree.expressions.operators.WrittenOperator
import me.eriknikli.rhenium.common.diagnostics.Diagnostic
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.IllegalBinaryOperation
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.IllegalCompoundAssignment
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.IllegalIncrement
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.MixedSignedness
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import org.antlr.v4.runtime.ParserRuleContext

enum class BinaryOperatorFailure {
    ILLEGAL_OPERATION,
    MIXED_SIGNEDNESS;

    fun toDiagnostic(
        parserContext: ParserRuleContext,
        left: ExpressionType,
        right: ExpressionType,
        operator: WrittenOperator
    ): Diagnostic = when (this) {
        ILLEGAL_OPERATION -> when (operator) {
            is WrittenOperator.Plain -> IllegalBinaryOperation(parserContext, left, right, operator)
            is WrittenOperator.Compound -> IllegalCompoundAssignment(parserContext, left, right, operator)
            is WrittenOperator.Increment -> IllegalIncrement(parserContext, left, operator)
        }

        MIXED_SIGNEDNESS -> MixedSignedness(parserContext, left, right, operator)
    }
}
