package me.eriknikli.rhenium.lowering.tree.statements

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.statements.ReturnStatement
import me.eriknikli.rhenium.lowering.INodeLowerer
import me.eriknikli.rhenium.lowering.actions.ReturnAction
import me.eriknikli.rhenium.lowering.tree.expressions.IExpressionLowerer
import javax.inject.Inject
import javax.inject.Singleton

interface IReturnStatementLowerer : INodeLowerer<ReturnStatement, ReturnAction>

@Singleton
class ReturnStatementLowerer
@Inject
constructor() : IReturnStatementLowerer {
    @Inject
    lateinit var expressionLowererProvider: Lazy<IExpressionLowerer>

    private val expressionLowerer by lazy { expressionLowererProvider.get() }

    override fun lower(node: ReturnStatement): ReturnAction =
        ReturnAction(node.value?.let { expressionLowerer.lower(it) })
}
