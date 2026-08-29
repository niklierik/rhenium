package me.eriknikli.rhenium.semanticAnalyzer.diagnostics

import me.eriknikli.rhenium.common.diagnostics.ContextDiagnostic
import me.eriknikli.rhenium.semanticContext.scope.Symbol
import org.antlr.v4.runtime.ParserRuleContext

data class InvalidLeftValueSymbol(
    override val parserContext: ParserRuleContext,
    val symbol: Symbol
) : ContextDiagnostic {
    override val message: String = "'${parserContext.text}' does not name a value."
}
