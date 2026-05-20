package me.eriknikli.rhenium.semanticContext.scope.types

import me.eriknikli.rhenium.semanticContext.scope.Symbol

interface ExpressionType : Symbol {
    fun canAssignTo(target: ExpressionType): Boolean
    fun canAssignToExplicit(target: ExpressionType): Boolean
}

fun ExpressionType.isNumeric(): Boolean {
    return this is SignedIntType || this is UnsignedIntType || this is FloatType
}

class InvalidType : ExpressionType {
    override fun canAssignTo(target: ExpressionType): Boolean {
        return false
    }

    override fun canAssignToExplicit(target: ExpressionType): Boolean {
        return false
    }

    override val cName: String = "void"

}

class BooleanType : ExpressionType {
    override val cName: String = "boolean_t"

    override fun canAssignTo(target: ExpressionType): Boolean {
        return (target is BooleanType)
    }

    override fun canAssignToExplicit(target: ExpressionType): Boolean {
        return (target is BooleanType)
    }

    override fun toString(): String {
        return "Boolean"
    }
}