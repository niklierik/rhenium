package me.eriknikli.rhenium.ast.diagnostics

import me.eriknikli.rhenium.ast.tree.expressions.literals.LiteralType
import me.eriknikli.rhenium.common.diagnostics.ContextDiagnostic
import org.antlr.v4.runtime.ParserRuleContext

data class InvalidValueOfLiteral(
    override val parserContext: ParserRuleContext,
    val value: String,
    val expectedType: LiteralType
) : ContextDiagnostic {
    override val message: String = "'$value' is not a valid $expectedType literal."
}
