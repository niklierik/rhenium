package me.eriknikli.rhenium.semanticContext.scope.types

import me.eriknikli.rhenium.semanticContext.scope.Symbol
import org.antlr.v4.runtime.ParserRuleContext

interface ExpressionType : Symbol {
    fun canAssignTo(target: ExpressionType): Boolean
    fun canAssignToExplicit(target: ExpressionType): Boolean
}

fun ExpressionType.isNumeric(): Boolean {
    return this is SignedIntType || this is UnsignedIntType || this is FloatType
}

class InvalidType : ExpressionType {
    override val declarationParserContext: ParserRuleContext? = null

    override fun canAssignTo(target: ExpressionType): Boolean {
        return false
    }

    override fun canAssignToExplicit(target: ExpressionType): Boolean {
        return false
    }

    override val cName: String = "void"

    override fun toString(): String {
        return "<invalid>"
    }
}

class BooleanType : ExpressionType {
    override val declarationParserContext: ParserRuleContext? = null
    
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