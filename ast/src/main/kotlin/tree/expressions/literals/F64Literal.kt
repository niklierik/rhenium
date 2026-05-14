package me.eriknikli.rhenium.ast.tree.expressions.literals

data class F64Literal(
    override val value: Double
) : Literal<Double>
