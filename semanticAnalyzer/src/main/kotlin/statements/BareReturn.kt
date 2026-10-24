package me.eriknikli.rhenium.semanticAnalyzer.statements

import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import me.eriknikli.rhenium.semanticContext.scope.types.InvalidType
import me.eriknikli.rhenium.semanticContext.scope.types.VoidType

fun ExpressionType.allowsBareReturn(): Boolean = this is VoidType || this is InvalidType
