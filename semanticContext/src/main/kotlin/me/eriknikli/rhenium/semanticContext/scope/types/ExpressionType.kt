package me.eriknikli.rhenium.semanticContext.scope.types

import me.eriknikli.rhenium.semanticContext.scope.Symbol
import org.antlr.v4.runtime.ParserRuleContext

interface ExpressionType : Symbol {
    val cFormat: String?

    fun canAssignTo(target: ExpressionType): Boolean
    fun canAssignToExplicit(target: ExpressionType): Boolean
}

fun ExpressionType.isNumeric(): Boolean {
    return this is SignedIntType || this is UnsignedIntType || this is FloatType
}

data object InvalidType : ExpressionType {
    override val declarationParserContext: ParserRuleContext? = null

    override val cFormat: String? = null

    override fun canAssignTo(target: ExpressionType): Boolean {
        return true
    }

    override fun canAssignToExplicit(target: ExpressionType): Boolean {
        return true
    }

    override val cName: String = "void"

    override fun toString(): String {
        return "<invalid>"
    }
}

data object BooleanType : ExpressionType {
    override val declarationParserContext: ParserRuleContext? = null

    override val cName: String = "boolean_t"

    override val cFormat: String = "\"%s\""

    override fun canAssignTo(target: ExpressionType): Boolean {
        return target is InvalidType || target is BooleanType
    }

    override fun canAssignToExplicit(target: ExpressionType): Boolean {
        return target is InvalidType || target is BooleanType
    }

    override fun toString(): String {
        return "Boolean"
    }
}