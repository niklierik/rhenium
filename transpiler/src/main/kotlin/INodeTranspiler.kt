package me.eriknikli.rhenium.transpiler

import me.eriknikli.rhenium.ast.tree.AstNode
import java.io.OutputStream

interface INodeTranspiler<T : AstNode> {
    fun transpile(node: T, output: OutputStream)
}