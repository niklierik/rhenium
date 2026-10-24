package me.eriknikli.rhenium.semanticContext.scope.types

import org.antlr.v4.runtime.ParserRuleContext

data object VoidType : ExpressionType {
    override val declarationParserContext: ParserRuleContext? = null

    override val cName: String = "void"

    override val cFormat: String? = null

    override fun canAssignTo(target: ExpressionType): Boolean {
        return target is InvalidType
    }

    override fun canAssignToExplicit(target: ExpressionType): Boolean {
        return target is InvalidType
    }

    override fun toString(): String {
        return "Void"
    }
}
