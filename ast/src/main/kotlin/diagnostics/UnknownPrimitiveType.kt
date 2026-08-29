package me.eriknikli.rhenium.ast.diagnostics

import me.eriknikli.rhenium.ast.tree.expressions.literals.LiteralType
import me.eriknikli.rhenium.common.diagnostics.ContextDiagnostic
import org.antlr.v4.runtime.ParserRuleContext

data class UnknownPrimitiveType(
    override val parserContext: ParserRuleContext,
    val actual: String,
    val expectedTypes: List<LiteralType>
) : ContextDiagnostic {
    override val message: String =
        "unknown primitive type '$actual', expected one of: ${expectedTypes.joinToString(", ")}."
}
