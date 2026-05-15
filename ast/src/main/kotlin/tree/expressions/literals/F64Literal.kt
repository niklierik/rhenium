package me.eriknikli.rhenium.ast.tree.expressions.literals

data class F64Literal(
    override val value: Double,
    override val textVersion: String
) : LiteralBase<Double>()
