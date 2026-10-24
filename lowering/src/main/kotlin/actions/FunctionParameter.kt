package me.eriknikli.rhenium.lowering.actions

import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType

data class FunctionParameter(
    val type: ExpressionType,
    val cName: String
)
