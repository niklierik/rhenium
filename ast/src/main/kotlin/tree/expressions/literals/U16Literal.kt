package me.eriknikli.rhenium.ast.tree.expressions.literals

data class U16Literal(
    override val value: UShort
) : Literal<UShort>
