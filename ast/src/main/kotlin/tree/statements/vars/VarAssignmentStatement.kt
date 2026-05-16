package me.eriknikli.rhenium.ast.tree.statements.vars

import me.eriknikli.rhenium.ast.tree.expressions.Expression
import me.eriknikli.rhenium.ast.tree.expressions.LeftValue

data class VarAssignmentStatement(
    val leftValue: LeftValue,
    val rightValue: Expression
)