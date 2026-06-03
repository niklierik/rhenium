package me.eriknikli.rhenium.ast.tree.expressions

import me.eriknikli.rhenium.semanticContext.tree.expressions.IdentifierContext
import org.antlr.v4.runtime.ParserRuleContext

data class Identifier(
    override val parserContext: ParserRuleContext,
    val id: String
) : LeftValue {
    override val context: IdentifierContext = IdentifierContext()

    override fun toString(): String {
        return id
    }
}
