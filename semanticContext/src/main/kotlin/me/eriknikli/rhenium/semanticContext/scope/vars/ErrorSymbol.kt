package me.eriknikli.rhenium.semanticContext.scope.vars

import me.eriknikli.rhenium.semanticContext.scope.LeftValueSymbol
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import me.eriknikli.rhenium.semanticContext.scope.types.InvalidType
import org.antlr.v4.runtime.ParserRuleContext

data object ErrorSymbol : LeftValueSymbol {
    override val cName: String = "re_error"
    override val declarationParserContext: ParserRuleContext? = null
    override val declaredType: ExpressionType = InvalidType
    override val mutable: Boolean = true
}
