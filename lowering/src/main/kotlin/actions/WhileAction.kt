package me.eriknikli.rhenium.lowering.actions

data class WhileAction(
    val condition: Action,
    val body: BlockAction
) : Action
