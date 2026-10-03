package me.eriknikli.rhenium.lowering.tree.statements

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.statements.vars.CompoundAssignmentStatement
import me.eriknikli.rhenium.lowering.INodeLowerer
import me.eriknikli.rhenium.lowering.actions.AssignmentAction
import me.eriknikli.rhenium.lowering.tree.expressions.IBinaryActionBuilder
import me.eriknikli.rhenium.lowering.tree.expressions.IExpressionLowerer
import me.eriknikli.rhenium.lowering.tree.expressions.ILeftValueLowerer
import javax.inject.Inject
import javax.inject.Singleton

interface ICompoundAssignmentLowerer : INodeLowerer<CompoundAssignmentStatement, AssignmentAction>

@Singleton
class CompoundAssignmentLowerer
@Inject
constructor() : ICompoundAssignmentLowerer {
    @Inject
    lateinit var expressionLowerer: Lazy<IExpressionLowerer>

    @Inject
    lateinit var leftValueLowerer: Lazy<ILeftValueLowerer>

    @Inject
    lateinit var binaryActionBuilder: IBinaryActionBuilder

    override fun lower(node: CompoundAssignmentStatement): AssignmentAction {
        val target = leftValueLowerer.get().lower(node.leftValue)

        return AssignmentAction(
            target,
            binaryActionBuilder.build(
                node.context.type,
                node.operator,
                target,
                expressionLowerer.get().lower(node.rightValue)
            )
        )
    }
}
