package me.eriknikli.rhenium.lowering.actions

data class CallAction(
    val cName: String,
    val arguments: List<Action>
) : Action
