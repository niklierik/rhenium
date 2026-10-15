package me.eriknikli.rhenium.lowering.tree.statements

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.statements.ExpressionStatement
import me.eriknikli.rhenium.ast.tree.statements.ForStatement
import me.eriknikli.rhenium.ast.tree.statements.Statement
import me.eriknikli.rhenium.ast.tree.statements.vars.CompoundAssignmentStatement
import me.eriknikli.rhenium.ast.tree.statements.vars.IncrementStatement
import me.eriknikli.rhenium.ast.tree.statements.vars.VarAssignmentStatement
import me.eriknikli.rhenium.lowering.INodeLowerer
import me.eriknikli.rhenium.lowering.actions.Action
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
    lateinit var blockStatementLowererProvider: Lazy<IBlockStatementLowerer>

    @Inject
    lateinit var varAssignmentLowererProvider: Lazy<IVarAssignmentLowerer>

    @Inject
    lateinit var compoundAssignmentLowererProvider: Lazy<ICompoundAssignmentLowerer>

    @Inject
    lateinit var incrementLowererProvider: Lazy<IIncrementLowerer>

    private val statementLowerer by lazy { statementLowererProvider.get() }
    private val expressionLowerer by lazy { expressionLowererProvider.get() }
    private val blockStatementLowerer by lazy { blockStatementLowererProvider.get() }
    private val varAssignmentLowerer by lazy { varAssignmentLowererProvider.get() }
    private val compoundAssignmentLowerer by lazy { compoundAssignmentLowererProvider.get() }
    private val incrementLowerer by lazy { incrementLowererProvider.get() }

    override fun lower(node: ForStatement): ForAction = ForAction(
        statementLowerer.lower(node.initClause),
        expressionLowerer.lower(node.condition),
        lowerIterationExpression(node.iterationExpression),
        blockStatementLowerer.lower(node.body)
    )

    private fun lowerIterationExpression(node: Statement): Action = when (node) {
        is VarAssignmentStatement -> varAssignmentLowerer.lower(node)
        is CompoundAssignmentStatement -> compoundAssignmentLowerer.lower(node)
        is IncrementStatement -> incrementLowerer.lower(node)
        is ExpressionStatement -> expressionLowerer.lower(node.expression)
        else -> throw IllegalStateException(
            "Unhandled node ${node.javaClass} and cannot lower it as an iteration expression."
        )
    }
}
