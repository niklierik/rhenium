package me.eriknikli.rhenium.semanticContext.scope.functions

import me.eriknikli.rhenium.semanticContext.scope.Symbol
import me.eriknikli.rhenium.semanticContext.utils.uniq
import org.antlr.v4.runtime.ParserRuleContext

class FunctionSymbol(
    val name: String,
    val signature: Signature,
    override val declarationParserContext: ParserRuleContext? = null
) : Symbol {
    override val cName: String = "re_${name}_${uniq()}"
}
