package me.eriknikli.rhenium.semanticAnalyzer.diagnostics

import me.eriknikli.rhenium.common.diagnostics.ContextDiagnostic
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import org.antlr.v4.runtime.ParserRuleContext

data class TypeMismatch(
    override val parserContext: ParserRuleContext,
    val actual: ExpressionType,
    val expected: List<ExpressionType>
) : ContextDiagnostic {
    override val message: String =
        "type mismatch, found $actual but expected ${expected.joinToString(" or ")}."
}
