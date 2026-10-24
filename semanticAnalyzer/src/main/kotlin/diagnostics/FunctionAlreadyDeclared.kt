package me.eriknikli.rhenium.semanticAnalyzer.diagnostics

import me.eriknikli.rhenium.common.diagnostics.ContextDiagnostic
import me.eriknikli.rhenium.common.diagnostics.Severity
import me.eriknikli.rhenium.common.location
import me.eriknikli.rhenium.semanticContext.scope.Symbol
import org.antlr.v4.runtime.ParserRuleContext

data class FunctionAlreadyDeclared(
    override val parserContext: ParserRuleContext,
    val name: String,
    val existingSymbol: Symbol
) : ContextDiagnostic {
    override val severity: Severity = Severity.ERROR
    override val message: String = "function '$name' is already declared" +
            (existingSymbol.declarationParserContext?.let { " at ${it.location}" } ?: "") + "."
}
