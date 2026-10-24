package me.eriknikli.rhenium.semanticAnalyzer.diagnostics

import me.eriknikli.rhenium.ast.tree.statements.ControlTransferStatement
import me.eriknikli.rhenium.common.diagnostics.ContextDiagnostic
import me.eriknikli.rhenium.common.diagnostics.Severity
import me.eriknikli.rhenium.common.location
import org.antlr.v4.runtime.ParserRuleContext

data class UnreachableStatement(
    override val parserContext: ParserRuleContext,
    val transfer: ControlTransferStatement
) : ContextDiagnostic {
    override val severity: Severity = Severity.WARNING
    override val message: String =
        "unreachable statement after '${transfer.keyword}' at ${transfer.parserContext.location}."
}
