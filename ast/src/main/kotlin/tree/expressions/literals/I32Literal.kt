package me.eriknikli.rhenium.ast.tree.expressions.literals

data class I32Literal(
    override val value: Int
) : Literal<Int>
