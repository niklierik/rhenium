package me.eriknikli.rhenium.ast.tree.expressions

import me.eriknikli.rhenium.semanticContext.tree.expressions.IdentifierContext

data class Identifier(
    val id: String
) : LeftValue {
    override val context: IdentifierContext = IdentifierContext()

    override fun toString(): String {
        return id
    }
}
