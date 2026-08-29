package me.eriknikli.rhenium.ast.diagnostics

import me.eriknikli.rhenium.common.diagnostics.Diagnostic

data object UnhandledParseRule : Diagnostic {
    override val line: Int = 0
    override val column: Int = 0
    override val message: String = "internal error: unhandled parse rule."
}
