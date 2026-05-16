package me.eriknikli.rhenium.semanticContext.scope.types

import me.eriknikli.rhenium.semanticContext.scope.Symbol

interface ExpressionType : Symbol {
    fun canAssignTo(target: ExpressionType): Boolean
    fun canAssignToExplicit(target: ExpressionType): Boolean
}