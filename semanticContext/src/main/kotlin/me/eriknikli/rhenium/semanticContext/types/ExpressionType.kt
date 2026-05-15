package me.eriknikli.rhenium.semanticContext.types

interface ExpressionType {
    val cTypeName: String

    fun canAssignTo(target: ExpressionType): Boolean
    fun canAssignToExplicit(target: ExpressionType): Boolean
}