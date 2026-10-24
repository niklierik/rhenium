package me.eriknikli.rhenium.semanticAnalyzer.diagnostics

import me.eriknikli.rhenium.common.diagnostics.ContextDiagnostic
import me.eriknikli.rhenium.common.diagnostics.Severity
import org.antlr.v4.runtime.ParserRuleContext

data class UnexpectedReturnValue(
    override val parserContext: ParserRuleContext
) : ContextDiagnostic {
    override val severity: Severity = Severity.ERROR
    override val message: String = "a Void function cannot return a value."
}
