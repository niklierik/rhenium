package me.eriknikli.rhenium.lowering.tree.statements

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.statements.ForStatement
import me.eriknikli.rhenium.lowering.INodeLowerer
import me.eriknikli.rhenium.lowering.actions.ForAction
import me.eriknikli.rhenium.lowering.tree.expressions.IExpressionLowerer
import javax.inject.Inject
import javax.inject.Singleton

interface IForStatementLowerer : INodeLowerer<ForStatement, ForAction>

@Singleton
class ForStatementLowerer
@Inject
constructor() : IForStatementLowerer {
    @Inject
    lateinit var statementLowererProvider: Lazy<IStatementLowerer>

    @Inject
    lateinit var expressionLowererProvider: Lazy<IExpressionLowerer>

    @Inject
    lateinit var iterationExpressionLowererProvider: Lazy<IIterationExpressionLowerer>

    @Inject
    lateinit var blockStatementLowererProvider: Lazy<IBlockStatementLowerer>

    private val statementLowerer by lazy { statementLowererProvider.get() }
    private val expressionLowerer by lazy { expressionLowererProvider.get() }
    private val iterationExpressionLowerer by lazy { iterationExpressionLowererProvider.get() }
    private val blockStatementLowerer by lazy { blockStatementLowererProvider.get() }

    override fun lower(node: ForStatement): ForAction = ForAction(
        statementLowerer.lower(node.initClause),
        expressionLowerer.lower(node.condition),
        iterationExpressionLowerer.lower(node.iterationExpression),
        blockStatementLowerer.lower(node.body)
    )
}
