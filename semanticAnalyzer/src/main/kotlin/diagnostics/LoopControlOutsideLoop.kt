package me.eriknikli.rhenium.semanticAnalyzer.diagnostics

import me.eriknikli.rhenium.ast.tree.statements.LoopControlStatement
import me.eriknikli.rhenium.common.diagnostics.ContextDiagnostic
import me.eriknikli.rhenium.common.diagnostics.Severity
import org.antlr.v4.runtime.ParserRuleContext

data class LoopControlOutsideLoop(
    val statement: LoopControlStatement
) : ContextDiagnostic {
    override val severity: Severity = Severity.ERROR
    override val parserContext: ParserRuleContext = statement.parserContext
    override val message: String = "'${statement.keyword}' is not inside a loop."
}
