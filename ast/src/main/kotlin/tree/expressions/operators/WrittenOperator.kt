package me.eriknikli.rhenium.ast.tree.expressions.operators

import me.eriknikli.rhenium.ast.tree.statements.vars.Step

sealed interface WrittenOperator {
    val spelling: String

    data class Plain(val operator: Operator) : WrittenOperator {
        override val spelling: String = operator.cString
    }

    data class Compound(val operator: Operator) : WrittenOperator {
        override val spelling: String = "${operator.cString}="
    }

    data class Increment(val step: Step) : WrittenOperator {
        override val spelling: String = step.spelling
    }
}
