package me.eriknikli.rhenium.semanticAnalyzer.diagnostics

import me.eriknikli.rhenium.common.diagnostics.ContextDiagnostic
import me.eriknikli.rhenium.common.location
import org.antlr.v4.runtime.ParserRuleContext

data class UnreachableStatement(
    override val parserContext: ParserRuleContext,
    val keyword: String,
    val loopControlParserContext: ParserRuleContext
) : ContextDiagnostic {
    override val message: String =
        "unreachable statement after '$keyword' at ${loopControlParserContext.location}."
}
