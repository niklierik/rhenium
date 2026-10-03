package me.eriknikli.rhenium.lowering.tree.expressions

import me.eriknikli.rhenium.lowering.actions.Action
import me.eriknikli.rhenium.lowering.actions.CastAction
import me.eriknikli.rhenium.lowering.actions.ConstantAction
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import me.eriknikli.rhenium.semanticContext.scope.types.UnsignedIntType
import javax.inject.Inject
import javax.inject.Singleton

interface INumericConstantFactory {
    fun build(type: ExpressionType, value: Any?): Action
}

@Singleton
class NumericConstantFactory
@Inject
constructor() : INumericConstantFactory {
    override fun build(type: ExpressionType, value: Any?): Action =
        CastAction(type, ConstantAction(cLiteral(type, value)))

    private fun cLiteral(type: ExpressionType, value: Any?): String {
        if (type is UnsignedIntType) {
            return "${value}u"
        }

        if (value == Long.MIN_VALUE) {
            return "(${Long.MIN_VALUE + 1}-1)"
        }

        return value.toString()
    }
}
