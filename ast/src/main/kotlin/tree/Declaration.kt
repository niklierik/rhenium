package me.eriknikli.rhenium.ast.tree

import me.eriknikli.rhenium.ast.tree.statements.BlockStatement

sealed interface Declaration : AstNode {
    val name: String
    val body: BlockStatement
}
