package me.eriknikli.rhenium.ast.diagnostics

import me.eriknikli.rhenium.ast.tree.expressions.literals.LiteralType
import me.eriknikli.rhenium.common.diagnostics.ContextDiagnostic
import org.antlr.v4.runtime.ParserRuleContext

data class UnexpectedPrimitiveType(
    override val parserContext: ParserRuleContext,
    val actualType: LiteralType,
    val expectedTypes: List<LiteralType>
) : ContextDiagnostic {
    override val message: String =
        "unexpected primitive type $actualType, expected one of: ${expectedTypes.joinToString(", ")}."
}
