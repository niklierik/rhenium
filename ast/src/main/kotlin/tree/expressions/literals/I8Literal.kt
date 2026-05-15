package me.eriknikli.rhenium.ast.tree.expressions.literals

data class I8Literal(
    override val value: Byte,
    override val textVersion: String
) : LiteralBase<Byte>()
