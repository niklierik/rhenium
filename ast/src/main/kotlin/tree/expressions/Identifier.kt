package me.eriknikli.rhenium.ast.tree.expressions

import me.eriknikli.rhenium.semanticContext.tree.expressions.IdentifierContext

data class Identifier(
    val id: String
) : Expression {
    override val context: IdentifierContext = IdentifierContext()

    override fun toString(): String {
        return id
    }
}
