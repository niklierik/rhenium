package me.eriknikli.rhenium.semanticAnalyzer.diagnostics

import me.eriknikli.rhenium.ast.tree.expressions.LeftValue
import me.eriknikli.rhenium.common.diagnostics.ContextDiagnostic
import org.antlr.v4.runtime.ParserRuleContext

data class ImmutableLeftValue(
    override val parserContext: ParserRuleContext,
    val leftValue: LeftValue
) : ContextDiagnostic {
    override val message: String =
        "cannot assign to '$leftValue', it is not mutable. Declare it with 'let'."
}
