package me.eriknikli.rhenium.semanticAnalyzer.diagnostics

import me.eriknikli.rhenium.ast.tree.expressions.Identifier
import me.eriknikli.rhenium.common.diagnostics.ContextDiagnostic
import me.eriknikli.rhenium.common.diagnostics.Severity
import org.antlr.v4.runtime.ParserRuleContext

data class NotAFunction(
    override val parserContext: ParserRuleContext,
    val callee: Identifier
) : ContextDiagnostic {
    override val severity: Severity = Severity.ERROR
    override val message: String = "'$callee' is not a function."
}
