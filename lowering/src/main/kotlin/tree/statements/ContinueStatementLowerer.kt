package me.eriknikli.rhenium.lowering.tree.statements

import me.eriknikli.rhenium.ast.tree.statements.ContinueStatement
import me.eriknikli.rhenium.lowering.INodeLowerer
import me.eriknikli.rhenium.lowering.actions.ContinueAction
import javax.inject.Inject
import javax.inject.Singleton

interface IContinueStatementLowerer : INodeLowerer<ContinueStatement, ContinueAction>

@Singleton
class ContinueStatementLowerer
@Inject
constructor() : IContinueStatementLowerer {
    override fun lower(node: ContinueStatement): ContinueAction = ContinueAction
}
