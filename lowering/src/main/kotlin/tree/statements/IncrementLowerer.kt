package me.eriknikli.rhenium.lowering.tree.statements

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.statements.vars.IncrementStatement
import me.eriknikli.rhenium.lowering.INodeLowerer
import me.eriknikli.rhenium.lowering.actions.AssignmentAction
import me.eriknikli.rhenium.lowering.actions.CastAction
import me.eriknikli.rhenium.lowering.actions.ConstantAction
import me.eriknikli.rhenium.lowering.tree.expressions.IBinaryActionFactory
import me.eriknikli.rhenium.lowering.tree.expressions.ILeftValueLowerer
import me.eriknikli.rhenium.semanticContext.scope.types.UnsignedIntType
import javax.inject.Inject
import javax.inject.Singleton

interface IIncrementLowerer : INodeLowerer<IncrementStatement, AssignmentAction>

@Singleton
class IncrementLowerer
@Inject
constructor() : IIncrementLowerer {
    @Inject
    lateinit var leftValueLowerer: Lazy<ILeftValueLowerer>

    @Inject
    lateinit var binaryActionFactory: IBinaryActionFactory

    override fun lower(node: IncrementStatement): AssignmentAction {
        val type = node.context.type
        val target = leftValueLowerer.get().lower(node.leftValue)
        val one = CastAction(type, ConstantAction(if (type is UnsignedIntType) "1u" else "1"))

        return AssignmentAction(target, binaryActionFactory.build(type, node.operator, target, one))
    }
}
