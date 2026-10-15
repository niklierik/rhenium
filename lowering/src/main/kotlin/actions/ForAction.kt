package me.eriknikli.rhenium.lowering.actions

data class ForAction(
    val initClause: Action,
    val condition: Action,
    val iterationExpression: Action,
    val body: BlockAction
) : Action
