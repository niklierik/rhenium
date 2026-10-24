package me.eriknikli.rhenium.semanticAnalyzer.diagnostics

import me.eriknikli.rhenium.common.diagnostics.ContextDiagnostic
import me.eriknikli.rhenium.common.diagnostics.Severity
import me.eriknikli.rhenium.common.location
import org.antlr.v4.runtime.ParserRuleContext

data class EntryAlreadyDeclared(
    override val parserContext: ParserRuleContext,
    val name: String,
    val existing: ParserRuleContext
) : ContextDiagnostic {
    override val severity: Severity = Severity.ERROR
    override val message: String = "entry '$name' is already declared at ${existing.location}."
}
