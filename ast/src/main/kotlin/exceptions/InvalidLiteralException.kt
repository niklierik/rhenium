package me.eriknikli.rhenium.ast.exceptions

import me.eriknikli.rhenium.ast.tree.expressions.literals.LiteralType
import org.antlr.v4.runtime.ParserRuleContext

open class InvalidLiteralException(
    parserContext: ParserRuleContext,
    value: String,
    expectedType: LiteralType,
    cause: Throwable? = null
) : SyntaxException(cause) {
}