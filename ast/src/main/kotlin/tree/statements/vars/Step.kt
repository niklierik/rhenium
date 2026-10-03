package me.eriknikli.rhenium.ast.tree.statements.vars

import me.eriknikli.rhenium.ast.tree.expressions.operators.Operator

enum class Step(val operator: Operator, val word: String) {
    UP(Operator.PLUS, "increment"),
    DOWN(Operator.MINUS, "decrement");

    val spelling: String = operator.cString + operator.cString
}
