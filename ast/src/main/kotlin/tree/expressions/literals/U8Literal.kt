package me.eriknikli.rhenium.ast.tree.expressions.literals

data class U8Literal(
    override val value: UByte
) : Literal<UByte>
