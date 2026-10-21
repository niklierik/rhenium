package me.eriknikli.rhenium.lowering.tree.statements

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.statements.ExpressionStatement
import me.eriknikli.rhenium.ast.tree.statements.IterationExpression
import me.eriknikli.rhenium.ast.tree.statements.vars.CompoundAssignmentStatement
import me.eriknikli.rhenium.ast.tree.statements.vars.IncrementStatement
import me.eriknikli.rhenium.ast.tree.statements.vars.VarAssignmentStatement
import me.eriknikli.rhenium.lowering.INodeLowerer
import me.eriknikli.rhenium.lowering.actions.Action
import me.eriknikli.rhenium.lowering.tree.expressions.IExpressionLowerer
import javax.inject.Inject
import javax.inject.Singleton

interface IIterationExpressionLowerer : INodeLowerer<IterationExpression, Action>

@Singleton
class IterationExpressionLowerer
@Inject
constructor() : IIterationExpressionLowerer {
    @Inject
    lateinit var varAssignmentLowererProvider: Lazy<IVarAssignmentLowerer>

    @Inject
    lateinit var compoundAssignmentLowererProvider: Lazy<ICompoundAssignmentLowerer>

    @Inject
    lateinit var incrementLowererProvider: Lazy<IIncrementLowerer>

    @Inject
    lateinit var expressionLowererProvider: Lazy<IExpressionLowerer>

    private val varAssignmentLowerer by lazy { varAssignmentLowererProvider.get() }
    private val compoundAssignmentLowerer by lazy { compoundAssignmentLowererProvider.get() }
    private val incrementLowerer by lazy { incrementLowererProvider.get() }
    private val expressionLowerer by lazy { expressionLowererProvider.get() }

    override fun lower(node: IterationExpression): Action = when (node) {
        is VarAssignmentStatement -> varAssignmentLowerer.lower(node)
        is CompoundAssignmentStatement -> compoundAssignmentLowerer.lower(node)
        is IncrementStatement -> incrementLowerer.lower(node)
        is ExpressionStatement -> expressionLowerer.lower(node.expression)
        else -> throw IllegalStateException(
            "Unhandled node ${node.javaClass} and cannot lower it as an iteration expression."
        )
    }
}
