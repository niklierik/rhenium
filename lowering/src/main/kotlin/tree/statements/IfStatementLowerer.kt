package me.eriknikli.rhenium.lowering.tree.statements

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.statements.BlockStatement
import me.eriknikli.rhenium.ast.tree.statements.ElseBranch
import me.eriknikli.rhenium.ast.tree.statements.IfStatement
import me.eriknikli.rhenium.lowering.INodeLowerer
import me.eriknikli.rhenium.lowering.actions.ElseBranchAction
import me.eriknikli.rhenium.lowering.actions.IfAction
import me.eriknikli.rhenium.lowering.tree.expressions.IExpressionLowerer
import javax.inject.Inject
import javax.inject.Singleton

interface IIfStatementLowerer : INodeLowerer<IfStatement, IfAction>

@Singleton
class IfStatementLowerer
@Inject
constructor() : IIfStatementLowerer {
    @Inject
    lateinit var expressionLowererProvider: Lazy<IExpressionLowerer>

    @Inject
    lateinit var blockStatementLowererProvider: Lazy<IBlockStatementLowerer>

    private val expressionLowerer by lazy { expressionLowererProvider.get() }
    private val blockStatementLowerer by lazy { blockStatementLowererProvider.get() }

    override fun lower(node: IfStatement): IfAction = IfAction(
        expressionLowerer.lower(node.condition),
        blockStatementLowerer.lower(node.thenBranch),
        node.elseBranch?.let { lowerElseBranch(it) }
    )

    private fun lowerElseBranch(elseBranch: ElseBranch): ElseBranchAction = when (elseBranch) {
        is BlockStatement -> blockStatementLowerer.lower(elseBranch)
        is IfStatement -> lower(elseBranch)
    }
}
