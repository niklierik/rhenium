package me.eriknikli.rhenium.lowering.tree.expressions

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.expressions.CallExpression
import me.eriknikli.rhenium.lowering.INodeLowerer
import me.eriknikli.rhenium.lowering.actions.CallAction
import javax.inject.Inject
import javax.inject.Singleton

interface ICallLowerer : INodeLowerer<CallExpression, CallAction>

@Singleton
class CallLowerer
@Inject
constructor() : ICallLowerer {
    @Inject
    lateinit var expressionLowererProvider: Lazy<IExpressionLowerer>

    private val expressionLowerer by lazy { expressionLowererProvider.get() }

    override fun lower(node: CallExpression): CallAction =
        CallAction(node.context.function.cName, node.arguments.map { expressionLowerer.lower(it) })
}
