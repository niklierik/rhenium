package me.eriknikli.rhenium.semanticAnalyzer.diagnostics

import me.eriknikli.rhenium.common.diagnostics.ContextDiagnostic
import org.antlr.v4.runtime.ParserRuleContext

data class LoopControlOutsideLoop(
    override val parserContext: ParserRuleContext,
    val keyword: String
) : ContextDiagnostic {
    override val message: String = "'$keyword' is not inside a loop."
}
