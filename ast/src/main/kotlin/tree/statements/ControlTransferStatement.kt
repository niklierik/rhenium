package me.eriknikli.rhenium.ast.tree.statements

sealed interface ControlTransferStatement : Statement {
    val keyword: String
}
