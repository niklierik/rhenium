package me.eriknikli.rhenium.lowering.tree.statements

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.statements.BlockStatement
import me.eriknikli.rhenium.lowering.INodeLowerer
import me.eriknikli.rhenium.lowering.actions.Block
import me.eriknikli.rhenium.lowering.actions.BlockAction
import javax.inject.Inject
import javax.inject.Singleton

interface IBlockStatementLowerer : INodeLowerer<BlockStatement, BlockAction>

@Singleton
class BlockStatementLowerer
@Inject
constructor() : IBlockStatementLowerer {
    @Inject
    lateinit var statementLowererProvider: Lazy<IStatementLowerer>

    private val statementLowerer by lazy { statementLowererProvider.get() }

    override fun lower(node: BlockStatement): BlockAction {
        val body = Block(mutableListOf())

        for (statement in node.statements) {
            body.actions.add(statementLowerer.lower(statement))
        }

        return BlockAction(body)
    }
}
