package me.eriknikli.rhenium.semanticAnalyzer.diagnostics

import me.eriknikli.rhenium.ast.tree.expressions.Identifier
import me.eriknikli.rhenium.common.diagnostics.ContextDiagnostic
import org.antlr.v4.runtime.ParserRuleContext

data class UnknownSymbol(
    override val parserContext: ParserRuleContext,
    val identifier: Identifier
) : ContextDiagnostic {
    override val message: String = "unknown symbol '$identifier'."
}
