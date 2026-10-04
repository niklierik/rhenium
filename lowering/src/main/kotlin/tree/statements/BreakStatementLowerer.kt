package me.eriknikli.rhenium.lowering.tree.statements

import me.eriknikli.rhenium.ast.tree.statements.BreakStatement
import me.eriknikli.rhenium.lowering.INodeLowerer
import me.eriknikli.rhenium.lowering.actions.BreakAction
import javax.inject.Inject
import javax.inject.Singleton

interface IBreakStatementLowerer : INodeLowerer<BreakStatement, BreakAction>

@Singleton
class BreakStatementLowerer
@Inject
constructor() : IBreakStatementLowerer {
    override fun lower(node: BreakStatement): BreakAction = BreakAction
}
