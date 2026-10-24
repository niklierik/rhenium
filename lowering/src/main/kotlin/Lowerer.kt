package me.eriknikli.rhenium.lowering

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.EntryDeclaration
import me.eriknikli.rhenium.ast.tree.RootNode
import me.eriknikli.rhenium.lowering.actions.Block
import me.eriknikli.rhenium.lowering.actions.CallAction
import me.eriknikli.rhenium.lowering.actions.ConstantAction
import me.eriknikli.rhenium.lowering.actions.ExpressionStatementAction
import me.eriknikli.rhenium.lowering.actions.FunctionAction
import me.eriknikli.rhenium.lowering.actions.FunctionSignature
import me.eriknikli.rhenium.lowering.actions.ProgramAction
import me.eriknikli.rhenium.lowering.actions.PrototypeAction
import me.eriknikli.rhenium.lowering.actions.ReturnAction
import me.eriknikli.rhenium.lowering.tree.declarations.IEntryDeclarationLowerer
import me.eriknikli.rhenium.lowering.tree.declarations.IFunctionDeclarationLowerer
import me.eriknikli.rhenium.semanticContext.scope.types.SignedIntType
import javax.inject.Inject
import javax.inject.Singleton

interface ILowerer {
    fun lower(node: RootNode, entry: EntryDeclaration): ProgramAction
}

@Singleton
class Lowerer
@Inject
constructor() : ILowerer {
    @Inject
    lateinit var functionDeclarationLowererProvider: Lazy<IFunctionDeclarationLowerer>

    @Inject
    lateinit var entryDeclarationLowererProvider: Lazy<IEntryDeclarationLowerer>

    private val functionDeclarationLowerer by lazy { functionDeclarationLowererProvider.get() }
    private val entryDeclarationLowerer by lazy { entryDeclarationLowererProvider.get() }

    override fun lower(node: RootNode, entry: EntryDeclaration): ProgramAction {
        val prototypes = node.functions.map { PrototypeAction(functionDeclarationLowerer.signatureOf(it)) }
        val functions = node.functions.map { functionDeclarationLowerer.lower(it) }
        val entries = node.entries.map { entryDeclarationLowerer.lower(it) }

        return ProgramAction(prototypes + functions + entries + main(entry))
    }

    private fun main(entry: EntryDeclaration): FunctionAction {
        val launch = CallAction(entryDeclarationLowerer.signatureOf(entry).cName, emptyList())
        val body = Block(mutableListOf(ExpressionStatementAction(launch), ReturnAction(ConstantAction("0"))))

        return FunctionAction(FunctionSignature("main", SignedIntType.I32, emptyList()), body)
    }
}
