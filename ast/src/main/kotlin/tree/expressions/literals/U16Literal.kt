package me.eriknikli.rhenium.ast.tree.expressions.literals

data class U16Literal(
    override val value: UShort,
    override val textVersion: String
) : LiteralBase<UShort>()
