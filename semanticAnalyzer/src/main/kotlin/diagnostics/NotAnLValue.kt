package me.eriknikli.rhenium.semanticAnalyzer.diagnostics

import me.eriknikli.rhenium.common.diagnostics.ContextDiagnostic
import org.antlr.v4.runtime.ParserRuleContext

data class NotAnLValue(
    override val parserContext: ParserRuleContext
) : ContextDiagnostic {
    override val message: String = "'${parserContext.text}' is not a valid assignment target."
}
