package me.eriknikli.rhenium.ast.exceptions

import me.eriknikli.rhenium.ast.tree.expressions.literals.LiteralType
import org.antlr.v4.runtime.ParserRuleContext

class UnknownPrimitiveTypeException(
    parserContext: ParserRuleContext,
    actual: String,
    vararg expectedTypes: LiteralType
) : SyntaxException() {

}