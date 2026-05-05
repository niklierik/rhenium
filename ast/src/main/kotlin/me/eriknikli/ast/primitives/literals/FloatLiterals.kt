package me.eriknikli.ast.primitives.literals

data class F32Literal(override val value: Float) : Literal<Float>

data class F64Literal(override val value: Double) : Literal<Double>
