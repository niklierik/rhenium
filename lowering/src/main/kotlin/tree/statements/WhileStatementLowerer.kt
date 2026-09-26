package me.eriknikli.rhenium.lowering.tree.statements

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.statements.WhileStatement
import me.eriknikli.rhenium.lowering.INodeLowerer
import me.eriknikli.rhenium.lowering.actions.WhileAction
import me.eriknikli.rhenium.lowering.tree.expressions.IExpressionLowerer
import javax.inject.Inject
import javax.inject.Singleton

interface IWhileStatementLowerer : INodeLowerer<WhileStatement, WhileAction>

@Singleton
class WhileStatementLowerer
@Inject
constructor() : IWhileStatementLowerer {
    @Inject
    lateinit var expressionLowererProvider: Lazy<IExpressionLowerer>

    @Inject
    lateinit var blockStatementLowererProvider: Lazy<IBlockStatementLowerer>

    private val expressionLowerer by lazy { expressionLowererProvider.get() }
    private val blockStatementLowerer by lazy { blockStatementLowererProvider.get() }

    override fun lower(node: WhileStatement): WhileAction = WhileAction(
        expressionLowerer.lower(node.condition),
        blockStatementLowerer.lower(node.body)
    )
}
