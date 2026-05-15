package me.eriknikli.rhenium.ast.tree.expressions.literals

data class U8Literal(
    override val value: UByte,
    override val textVersion: String
) : LiteralBase<UByte>()
