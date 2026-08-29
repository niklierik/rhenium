package me.eriknikli.rhenium.semanticContext.scope.types

import org.antlr.v4.runtime.ParserRuleContext

enum class SignedIntType(
    val index: Int,
    override val cName: String
) : ExpressionType {
    I64(0, "int64_t"), I32(1, "int32_t"), I16(2, "int16_t"), I8(3, "int8_t");

    override val declarationParserContext: ParserRuleContext? = null

    override fun canAssignTo(target: ExpressionType): Boolean {
        if (target is InvalidType) {
            return true
        }
        if (target !is SignedIntType) {
            return false
        }
        return index >= target.index
    }

    override fun canAssignToExplicit(target: ExpressionType): Boolean {
        return target is InvalidType || target is FloatType || target is SignedIntType || target is UnsignedIntType
    }
}

enum class UnsignedIntType(
    val index: Int,
    override val cName: String
) : ExpressionType {
    U64(0, "uint64_t"),
    U32(1, "uint32_t"),
    U16(2, "uint16_t"),
    U8(3, "uint8_t");

    override val declarationParserContext: ParserRuleContext? = null

    override fun canAssignTo(target: ExpressionType): Boolean {
        if (target is InvalidType) {
            return true
        }
        if (target !is UnsignedIntType) {
            return false
        }
        return index >= target.index
    }

    override fun canAssignToExplicit(target: ExpressionType): Boolean {
        return target is InvalidType || target is FloatType || target is SignedIntType || target is UnsignedIntType
    }
}

enum class FloatType(
    val index: Int,
    override val cName: String
) : ExpressionType {
    F64(0, "float64_t"),
    F32(1, "float32_t");

    override val declarationParserContext: ParserRuleContext? = null

    override fun canAssignTo(target: ExpressionType): Boolean {
        if (target is InvalidType) {
            return true
        }
        if (target !is FloatType) {
            return false
        }
        return index >= target.index
    }

    override fun canAssignToExplicit(target: ExpressionType): Boolean {
        return target is InvalidType || target is FloatType || target is SignedIntType || target is UnsignedIntType
    }
}

fun numericTypes(): Set<ExpressionType> {
    val signed = SignedIntType.entries.toSet()
    val unsigned = UnsignedIntType.entries.toSet()
    val float = FloatType.entries.toSet()

    return signed.union(unsigned).union(float)
}