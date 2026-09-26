package me.eriknikli.rhenium.semanticAnalyzer.diagnostics

import me.eriknikli.rhenium.common.diagnostics.ContextDiagnostic
import me.eriknikli.rhenium.common.location
import me.eriknikli.rhenium.semanticContext.scope.Symbol
import org.antlr.v4.runtime.ParserRuleContext

data class VariableShadowsOuter(
    override val parserContext: ParserRuleContext,
    val name: String,
    val shadowedSymbol: Symbol
) : ContextDiagnostic {
    override val message: String = "variable '$name' shadows variable '$name'" +
            (shadowedSymbol.declarationParserContext?.let { " declared at ${it.location}" } ?: "") + "."
}
