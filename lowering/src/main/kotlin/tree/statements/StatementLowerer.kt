package me.eriknikli.rhenium.lowering.tree.statements

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.statements.BlockStatement
import me.eriknikli.rhenium.ast.tree.statements.ExpressionStatement
import me.eriknikli.rhenium.ast.tree.statements.IfStatement
import me.eriknikli.rhenium.ast.tree.statements.PrintStatement
import me.eriknikli.rhenium.ast.tree.statements.Statement
import me.eriknikli.rhenium.ast.tree.statements.WhileStatement
import me.eriknikli.rhenium.ast.tree.statements.vars.VarAssignmentStatement
import me.eriknikli.rhenium.ast.tree.statements.vars.VarDeclarationStatement
import me.eriknikli.rhenium.lowering.INodeLowerer
import me.eriknikli.rhenium.lowering.actions.Action
import javax.inject.Inject
import javax.inject.Singleton

interface IStatementLowerer : INodeLowerer<Statement, Action>

@Singleton
class StatementLowerer
@Inject
constructor() : IStatementLowerer {
    @Inject
    lateinit var varDeclarationLowerer: Lazy<IVarDeclarationLowerer>

    @Inject
    lateinit var varAssignmentLowerer: Lazy<IVarAssignmentLowerer>

    @Inject
    lateinit var expressionStatementLowerer: Lazy<IExpressionStatementLowerer>

    @Inject
    lateinit var printStatementLowerer: Lazy<IPrintStatementLowerer>

    @Inject
    lateinit var blockStatementLowerer: Lazy<IBlockStatementLowerer>

    @Inject
    lateinit var whileStatementLowerer: Lazy<IWhileStatementLowerer>

    @Inject
    lateinit var ifStatementLowerer: Lazy<IIfStatementLowerer>

    override fun lower(node: Statement): Action {
        return when (node) {
            is VarDeclarationStatement -> varDeclarationLowerer.get().lower(node)
            is VarAssignmentStatement -> varAssignmentLowerer.get().lower(node)
            is ExpressionStatement -> expressionStatementLowerer.get().lower(node)
            is PrintStatement -> printStatementLowerer.get().lower(node)
            is BlockStatement -> blockStatementLowerer.get().lower(node)
            is WhileStatement -> whileStatementLowerer.get().lower(node)
            is IfStatement -> ifStatementLowerer.get().lower(node)
            else -> throw IllegalStateException("Unhandled node ${node.javaClass} and cannot lower it as statement.")
        }
    }
}
