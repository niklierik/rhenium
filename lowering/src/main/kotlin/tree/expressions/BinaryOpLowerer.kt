package me.eriknikli.rhenium.lowering.tree.expressions

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.expressions.operators.BinaryOpExpression
import me.eriknikli.rhenium.lowering.INodeLowerer
import me.eriknikli.rhenium.lowering.actions.Action
import javax.inject.Inject
import javax.inject.Singleton

interface IBinaryOpLowerer : INodeLowerer<BinaryOpExpression, Action>

@Singleton
class BinaryOpLowerer
@Inject
constructor() : IBinaryOpLowerer {
    @Inject
    lateinit var expressionLowererProvider: Lazy<IExpressionLowerer>

    @Inject
    lateinit var binaryActionFactory: IBinaryActionFactory

    private val expressionLowerer by lazy { expressionLowererProvider.get() }

    override fun lower(node: BinaryOpExpression): Action {
        return binaryActionFactory.build(
            node.context.type,
            node.operator,
            expressionLowerer.lower(node.left),
            expressionLowerer.lower(node.right)
        )
    }
}
