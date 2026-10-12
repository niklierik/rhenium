package me.eriknikli.rhenium.ast.diagnostics

import me.eriknikli.rhenium.common.diagnostics.Diagnostic
import me.eriknikli.rhenium.common.diagnostics.Severity

data class SyntaxError(
    override val line: Int,
    override val column: Int,
    override val message: String
) : Diagnostic {
    override val severity: Severity = Severity.ERROR
}
