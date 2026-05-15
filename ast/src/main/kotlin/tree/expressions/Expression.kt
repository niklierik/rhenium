package me.eriknikli.rhenium.ast.tree.expressions

import me.eriknikli.rhenium.ast.tree.AstNode
import me.eriknikli.rhenium.semanticContext.tree.expressions.ExpressionContext

interface Expression : AstNode {
    val context: ExpressionContext
}