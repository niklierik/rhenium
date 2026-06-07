package me.eriknikli.rhenium.ast.exceptions

import me.eriknikli.rhenium.ast.tree.expressions.literals.LiteralType
import org.antlr.v4.runtime.ParserRuleContext

class InvalidPrimitiveTypeException(
    parserContext: ParserRuleContext,
    actualType: LiteralType,
    vararg expectedTypes: LiteralType
) : SyntaxException() {

}