package me.eriknikli.rhenium.ast.tree

import me.eriknikli.rhenium.ast.tree.expressions.Identifier
import me.eriknikli.rhenium.semanticContext.tree.declarations.ParameterContext
import org.antlr.v4.runtime.ParserRuleContext

data class Parameter(
    override val parserContext: ParserRuleContext,
    val mutable: Boolean,
    val name: String,
    val type: Identifier
) : AstNode {
    val context: ParameterContext = ParameterContext()
}
