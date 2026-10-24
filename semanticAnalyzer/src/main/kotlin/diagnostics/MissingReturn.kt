package me.eriknikli.rhenium.semanticAnalyzer.diagnostics

import me.eriknikli.rhenium.common.diagnostics.ContextDiagnostic
import me.eriknikli.rhenium.common.diagnostics.Severity
import org.antlr.v4.runtime.ParserRuleContext

data class MissingReturn(
    override val parserContext: ParserRuleContext,
    val name: String
) : ContextDiagnostic {
    override val severity: Severity = Severity.ERROR
    override val message: String = "function '$name' does not return a value on every path."
}
