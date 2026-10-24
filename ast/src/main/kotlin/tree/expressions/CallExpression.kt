package me.eriknikli.rhenium.ast.tree.expressions

import me.eriknikli.rhenium.semanticContext.tree.expressions.CallContext
import org.antlr.v4.runtime.ParserRuleContext

data class CallExpression(
    override val parserContext: ParserRuleContext,
    val callee: Identifier,
    val arguments: List<Expression>
) : Expression {
    override val context: CallContext = CallContext()
}
