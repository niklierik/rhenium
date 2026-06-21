package me.eriknikli.rhenium.semanticContext.scope

import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import org.antlr.v4.runtime.ParserRuleContext

interface Symbol {
    val cName: String
    val declarationParserContext: ParserRuleContext?
}

interface LeftValueSymbol : Symbol {
    val declaredType: ExpressionType
    val mutable: Boolean
}