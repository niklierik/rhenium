package me.eriknikli.rhenium.semanticAnalyzer.diagnostics

import me.eriknikli.rhenium.common.diagnostics.ContextDiagnostic
import me.eriknikli.rhenium.common.diagnostics.Severity
import me.eriknikli.rhenium.common.sourceText
import org.antlr.v4.runtime.ParserRuleContext

data class VoidHasNoValue(
    override val parserContext: ParserRuleContext
) : ContextDiagnostic {
    override val severity: Severity = Severity.ERROR
    override val message: String = "'${parserContext.sourceText}' has no value."
}
