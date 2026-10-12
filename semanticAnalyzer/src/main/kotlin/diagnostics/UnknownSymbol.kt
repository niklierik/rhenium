package me.eriknikli.rhenium.semanticAnalyzer.diagnostics

import me.eriknikli.rhenium.ast.tree.expressions.Identifier
import me.eriknikli.rhenium.common.diagnostics.ContextDiagnostic
import me.eriknikli.rhenium.common.diagnostics.Severity
import org.antlr.v4.runtime.ParserRuleContext

data class UnknownSymbol(
    override val parserContext: ParserRuleContext,
    val identifier: Identifier
) : ContextDiagnostic {
    override val severity: Severity = Severity.ERROR
    override val message: String = "unknown symbol '$identifier'."
}
