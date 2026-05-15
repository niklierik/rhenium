package me.eriknikli.rhenium.ast.tree.expressions.literals

data class F32Literal(
    override val value: Float,
    override val textVersion: String
) : LiteralBase<Float>()
