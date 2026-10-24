package me.eriknikli.rhenium.lowering.actions

data class FunctionAction(
    val signature: FunctionSignature,
    val body: Block
) : Action
