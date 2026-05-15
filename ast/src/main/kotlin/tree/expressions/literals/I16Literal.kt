package me.eriknikli.rhenium.ast.tree.expressions.literals

data class I16Literal(
    override val value: Short,
    override val textVersion: String
) : LiteralBase<Short>()
