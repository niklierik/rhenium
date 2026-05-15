package me.eriknikli.rhenium.ast.tree.statements

import me.eriknikli.rhenium.ast.tree.AstNode
import me.eriknikli.rhenium.semanticContext.tree.statements.StatementContext

interface Statement : AstNode {
    val context: StatementContext
}