package me.eriknikli.rhenium.common.diagnostics

import me.eriknikli.rhenium.common.column
import me.eriknikli.rhenium.common.line
import org.antlr.v4.runtime.ParserRuleContext

interface ContextDiagnostic : Diagnostic {
    val parserContext: ParserRuleContext

    override val line: Int
        get() = parserContext.line

    override val column: Int
        get() = parserContext.column
}
