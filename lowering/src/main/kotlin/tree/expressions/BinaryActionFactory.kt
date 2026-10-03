package me.eriknikli.rhenium.lowering.tree.expressions

import me.eriknikli.rhenium.ast.tree.expressions.operators.Operator
import me.eriknikli.rhenium.lowering.actions.Action
import me.eriknikli.rhenium.lowering.actions.BinaryAction
import me.eriknikli.rhenium.lowering.actions.CastAction
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import me.eriknikli.rhenium.semanticContext.scope.types.UnsignedIntType
import me.eriknikli.rhenium.semanticContext.scope.types.detourType
import me.eriknikli.rhenium.semanticContext.scope.types.isNumeric
import javax.inject.Inject
import javax.inject.Singleton

interface IBinaryActionFactory {
    fun build(type: ExpressionType, operator: Operator, left: Action, right: Action): Action
}

@Singleton
class BinaryActionFactory
@Inject
constructor() : IBinaryActionFactory {
    override fun build(type: ExpressionType, operator: Operator, left: Action, right: Action): Action {
        if (!type.isNumeric()) {
            return BinaryAction(operator.cString, left, right)
        }

        val detour = if (operator in DETOURED_OPERATORS) type.detourType else null

        return CastAction(type, BinaryAction(operator.cString, left.through(detour), right.through(detour)))
    }

    private fun Action.through(detour: UnsignedIntType?): Action =
        if (detour == null) this else CastAction(detour, this)
}

private val DETOURED_OPERATORS = setOf(Operator.PLUS, Operator.MINUS, Operator.STAR)
