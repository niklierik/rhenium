package me.eriknikli.rhenium.ast.exceptions

import me.eriknikli.rhenium.ast.tree.expressions.literals.LiteralType
import me.eriknikli.rhenium.common.location
import org.antlr.v4.runtime.ParserRuleContext

open class InvalidValueOfLiteralException(
    val parserContext: ParserRuleContext,
    val value: String,
    val expectedType: LiteralType,
    cause: Throwable? = null
) : SyntaxException("${parserContext.location}: '$value' is not a valid $expectedType literal.", cause)
