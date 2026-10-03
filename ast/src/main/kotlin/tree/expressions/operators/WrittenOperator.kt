package me.eriknikli.rhenium.ast.tree.expressions.operators

sealed interface WrittenOperator {
    val operator: Operator
    val spelling: String

    data class Plain(override val operator: Operator) : WrittenOperator {
        override val spelling: String = operator.cString
    }

    data class Compound(override val operator: Operator) : WrittenOperator {
        override val spelling: String = "${operator.cString}="
    }

    data class Increment(override val operator: Operator) : WrittenOperator {
        override val spelling: String = operator.cString + operator.cString
        val word: String = if (operator == Operator.PLUS) "increment" else "decrement"
    }
}
