package me.eriknikli.ast.primitives

import me.eriknikli.ast.Expression

data class GroupExpression(val expression: Expression) : Expression
