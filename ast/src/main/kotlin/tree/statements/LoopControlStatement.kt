package me.eriknikli.rhenium.ast.tree.statements

sealed interface LoopControlStatement : Statement {
    val keyword: String
}
