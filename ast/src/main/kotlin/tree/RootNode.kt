package me.eriknikli.rhenium.ast.tree

import me.eriknikli.rhenium.ast.tree.statements.Statement
import org.antlr.v4.runtime.ParserRuleContext

data class RootNode(
    override val parserContext: ParserRuleContext,
    val statements: List<Statement>,
) : AstNode
