package me.eriknikli.rhenium.ast.tree.expressions.literals

data class U32Literal(
    override val value: UInt
) : Literal<UInt>
