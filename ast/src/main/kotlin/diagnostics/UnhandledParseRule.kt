package me.eriknikli.rhenium.ast.diagnostics

import me.eriknikli.rhenium.common.diagnostics.Diagnostic
import me.eriknikli.rhenium.common.diagnostics.Severity

data object UnhandledParseRule : Diagnostic {
    override val severity: Severity = Severity.ERROR
    override val line: Int = 0
    override val column: Int = 0
    override val message: String = "internal error: unhandled parse rule."
}
