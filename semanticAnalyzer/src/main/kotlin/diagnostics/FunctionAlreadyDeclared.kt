package me.eriknikli.rhenium.semanticAnalyzer.diagnostics

import me.eriknikli.rhenium.common.diagnostics.ContextDiagnostic
import me.eriknikli.rhenium.common.diagnostics.Severity
import me.eriknikli.rhenium.common.location
import org.antlr.v4.runtime.ParserRuleContext

data class FunctionAlreadyDeclared(
    override val parserContext: ParserRuleContext,
    val name: String,
    val existing: ParserRuleContext?
) : ContextDiagnostic {
    override val severity: Severity = Severity.ERROR
    override val message: String = "function '$name' is already declared" +
            (existing?.let { " at ${it.location}" } ?: "") + "."
}
