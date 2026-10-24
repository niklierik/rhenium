package me.eriknikli.rhenium.semanticAnalyzer.diagnostics

import me.eriknikli.rhenium.common.diagnostics.ContextDiagnostic
import me.eriknikli.rhenium.common.diagnostics.Severity
import org.antlr.v4.runtime.ParserRuleContext

data class ArgumentCountMismatch(
    override val parserContext: ParserRuleContext,
    val name: String,
    val expected: Int,
    val actual: Int
) : ContextDiagnostic {
    override val severity: Severity = Severity.ERROR
    override val message: String =
        "'$name' takes $expected argument${if (expected == 1) "" else "s"}, but $actual ${if (actual == 1) "was" else "were"} given."
}
