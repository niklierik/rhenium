package me.eriknikli.ast.primitives.literals

data class U64Literal(
    override val value: ULong
) : Literal<ULong>

data class U32Literal(
    override val value: UInt
) : Literal<UInt>

data class U16Literal(
    override val value: UShort
) : Literal<UShort>

data class U8Literal(
    override val value: UByte
) : Literal<UByte>
