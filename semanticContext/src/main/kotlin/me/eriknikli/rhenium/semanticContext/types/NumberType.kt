package me.eriknikli.rhenium.semanticContext.types

enum class SignedIntType(
    private val index: Int,
    override val cTypeName: String
) : ExpressionType {
    I64(0, "int64_t"), I32(1, "int32_t"), I16(2, "int16_t"), I8(3, "int8_t");

    override fun canAssignTo(target: ExpressionType): Boolean {
        if (target !is SignedIntType) {
            return false
        }
        return index < target.index
    }

    override fun canAssignToExplicit(target: ExpressionType): Boolean {
        return target is FloatType || target is SignedIntType || target is UnsignedIntType
    }
}

enum class UnsignedIntType(
    private val index: Int,
    override val cTypeName: String
) : ExpressionType {
    U64(0, "uint64_t"),
    U32(1, "uint32_t"),
    U16(2, "uint16_t"),
    U8(3, "uint8_t");

    override fun canAssignTo(target: ExpressionType): Boolean {
        if (target !is UnsignedIntType) {
            return false
        }
        return index < target.index
    }

    override fun canAssignToExplicit(target: ExpressionType): Boolean {
        return target is FloatType || target is SignedIntType || target is UnsignedIntType
    }
}

enum class FloatType(
    private val index: Int,
    override val cTypeName: String
) : ExpressionType {
    F64(0, "float64_t"),
    F32(1, "float32_t");

    override fun canAssignTo(target: ExpressionType): Boolean {
        if (target !is FloatType) {
            return false
        }
        return index < target.index
    }

    override fun canAssignToExplicit(target: ExpressionType): Boolean {
        return target is FloatType || target is SignedIntType || target is UnsignedIntType
    }
}