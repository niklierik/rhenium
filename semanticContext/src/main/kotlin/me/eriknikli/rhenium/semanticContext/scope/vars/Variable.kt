package me.eriknikli.rhenium.semanticContext.scope.vars

import me.eriknikli.rhenium.semanticContext.scope.Symbol
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import me.eriknikli.rhenium.semanticContext.utils.uniq

class Variable(
    val name: String,
    val type: ExpressionType,
    val mutable: Boolean,
) : Symbol {
    override val cName: String = "re_${name}_${uniq()}"
}