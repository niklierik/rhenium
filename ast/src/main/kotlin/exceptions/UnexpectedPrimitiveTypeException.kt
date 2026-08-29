package me.eriknikli.rhenium.ast.exceptions

import me.eriknikli.rhenium.ast.tree.expressions.literals.LiteralType
import me.eriknikli.rhenium.common.location
import org.antlr.v4.runtime.ParserRuleContext

class UnexpectedPrimitiveTypeException(
    val parserContext: ParserRuleContext,
    val actualType: LiteralType,
    vararg val expectedTypes: LiteralType
) : SyntaxException(
    "${parserContext.location}: unexpected primitive type $actualType, expected one of: " +
            expectedTypes.joinToString(", ") + "."
)
