package me.eriknikli.ast.primitives.literals

data class I64Literal(
    override val value: Long
) : Literal<Long>

data class I32Literal(
    override val value: Int
) : Literal<Int>

data class I16Literal(
    override val value: Short
) : Literal<Short>

data class I8Literal(
    override val value: Byte
) : Literal<Byte>
