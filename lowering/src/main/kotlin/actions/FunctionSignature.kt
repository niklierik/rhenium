package me.eriknikli.rhenium.lowering.actions

import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType

data class FunctionSignature(
    val cName: String,
    val returnType: ExpressionType,
    val parameters: List<FunctionParameter>
)
