package me.eriknikli.rhenium.ast.tree

import me.eriknikli.rhenium.ast.tree.expressions.literals.Literal

data class RootNode(
    val literals: List<Literal<*>>
)
