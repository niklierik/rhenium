package me.eriknikli.rhenium.semanticContext.scope.vars

import me.eriknikli.rhenium.semanticContext.scope.LeftValueSymbol
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import me.eriknikli.rhenium.semanticContext.utils.uniq
import org.antlr.v4.runtime.ParserRuleContext

class Variable(
    val name: String,
    override val declaredType: ExpressionType,
    override val mutable: Boolean,
    override val declarationParserContext: ParserRuleContext? = null
) : LeftValueSymbol {
    override val cName: String = "re_${name}_${uniq()}"
}