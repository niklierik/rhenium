package me.eriknikli.rhenium.semanticContext.scope.functions

import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType

data class Signature(
    val parameterTypes: List<ExpressionType>,
    val returnType: ExpressionType
)
