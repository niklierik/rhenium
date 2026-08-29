package me.eriknikli.rhenium.semanticAnalyzer.diagnostics

import me.eriknikli.rhenium.common.diagnostics.ContextDiagnostic
import me.eriknikli.rhenium.common.location
import me.eriknikli.rhenium.semanticContext.scope.Symbol
import org.antlr.v4.runtime.ParserRuleContext

data class VariableAlreadyDeclared(
    override val parserContext: ParserRuleContext,
    val name: String,
    val existingSymbol: Symbol? = null
) : ContextDiagnostic {
    override val message: String = "variable '$name' is already declared" +
            (existingSymbol?.declarationParserContext?.let { " at ${it.location}" } ?: "") + "."
}
