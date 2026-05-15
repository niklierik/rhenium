package me.eriknikli.rhenium.ast.tree.expressions.literals

data class U64Literal(
    override val value: ULong,
    override val textVersion: String
) : LiteralBase<ULong>()
