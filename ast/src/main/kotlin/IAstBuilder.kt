package me.eriknikli.rhenium.ast

import me.eriknikli.rhenium.ast.tree.RootNode
import org.antlr.v4.runtime.CharStream

interface IAstBuilder {
    fun parse(stream: CharStream): RootNode
}