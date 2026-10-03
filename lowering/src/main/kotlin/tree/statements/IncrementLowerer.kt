package me.eriknikli.rhenium.lowering.tree.statements

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.statements.vars.IncrementStatement
import me.eriknikli.rhenium.lowering.INodeLowerer
import me.eriknikli.rhenium.lowering.actions.AssignmentAction
import me.eriknikli.rhenium.lowering.tree.expressions.IBinaryActionFactory
import me.eriknikli.rhenium.lowering.tree.expressions.ILeftValueLowerer
import me.eriknikli.rhenium.lowering.tree.expressions.INumericConstantFactory
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

    @Inject
    lateinit var numericConstantFactory: INumericConstantFactory

    override fun lower(node: IncrementStatement): AssignmentAction {
        val type = node.context.type
        val target = leftValueLowerer.get().lower(node.leftValue)
        val one = numericConstantFactory.build(type, 1)

        return AssignmentAction(target, binaryActionFactory.build(type, node.step.operator, target, one))
    }
}
