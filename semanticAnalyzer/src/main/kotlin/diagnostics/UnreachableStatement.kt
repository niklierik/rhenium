package me.eriknikli.rhenium.semanticAnalyzer.diagnostics

import me.eriknikli.rhenium.ast.tree.statements.LoopControlStatement
import me.eriknikli.rhenium.common.diagnostics.ContextDiagnostic
import me.eriknikli.rhenium.common.diagnostics.Severity
import me.eriknikli.rhenium.common.location
import org.antlr.v4.runtime.ParserRuleContext

data class UnreachableStatement(
    override val parserContext: ParserRuleContext,
    val loopControl: LoopControlStatement
) : ContextDiagnostic {
    override val severity: Severity = Severity.ERROR
    override val message: String =
        "unreachable statement after '${loopControl.keyword}' at ${loopControl.parserContext.location}."
}
