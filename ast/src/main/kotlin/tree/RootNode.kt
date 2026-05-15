package me.eriknikli.rhenium.ast.tree

import me.eriknikli.rhenium.ast.tree.statements.Statement

data class RootNode(
    val statements: List<Statement>
) : AstNode
