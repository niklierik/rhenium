package me.eriknikli.rhenium.lowering.actions

data class IfAction(
    val condition: Action,
    val thenBranch: BlockAction,
    val elseBranch: ElseBranchAction?
) : ElseBranchAction
