package me.eriknikli.rhenium.semanticAnalyzer.diagnostics

import me.eriknikli.rhenium.common.diagnostics.ContextDiagnostic
import me.eriknikli.rhenium.common.diagnostics.Severity
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import org.antlr.v4.runtime.ParserRuleContext

data class MissingReturnValue(
    override val parserContext: ParserRuleContext,
    val returnType: ExpressionType
) : ContextDiagnostic {
    override val severity: Severity = Severity.ERROR
    override val message: String = "'return' needs a value of type $returnType."
}
