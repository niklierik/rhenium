package me.eriknikli.rhenium.ast.exceptions

import me.eriknikli.rhenium.ast.tree.expressions.literals.LiteralType
import me.eriknikli.rhenium.common.location
import org.antlr.v4.runtime.ParserRuleContext

class UnknownPrimitiveTypeException(
    val parserContext: ParserRuleContext,
    val actual: String,
    vararg val expectedTypes: LiteralType
) : SyntaxException(
    "${parserContext.location}: unknown primitive type '$actual', expected one of: " +
            expectedTypes.joinToString(", ") + "."
)
