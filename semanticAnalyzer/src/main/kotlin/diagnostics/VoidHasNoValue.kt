package me.eriknikli.rhenium.semanticAnalyzer.diagnostics

import me.eriknikli.rhenium.common.diagnostics.ContextDiagnostic
import me.eriknikli.rhenium.common.diagnostics.Severity
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.misc.Interval

data class VoidHasNoValue(
    override val parserContext: ParserRuleContext
) : ContextDiagnostic {
    override val severity: Severity = Severity.ERROR
    override val message: String =
        "'${parserContext.start.inputStream.getText(Interval(parserContext.start.startIndex, parserContext.stop.stopIndex))}' has no value."
}
