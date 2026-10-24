package me.eriknikli.rhenium.lowering.tree.declarations

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.EntryDeclaration
import me.eriknikli.rhenium.lowering.INodeLowerer
import me.eriknikli.rhenium.lowering.actions.FunctionAction
import me.eriknikli.rhenium.lowering.actions.FunctionSignature
import me.eriknikli.rhenium.lowering.tree.statements.IBlockStatementLowerer
import me.eriknikli.rhenium.semanticContext.scope.types.VoidType
import javax.inject.Inject
import javax.inject.Singleton

interface IEntryDeclarationLowerer : INodeLowerer<EntryDeclaration, FunctionAction> {
    fun signatureOf(entry: EntryDeclaration): FunctionSignature
}

@Singleton
class EntryDeclarationLowerer
@Inject
constructor() : IEntryDeclarationLowerer {
    @Inject
    lateinit var blockStatementLowerer: Lazy<IBlockStatementLowerer>

    override fun signatureOf(entry: EntryDeclaration): FunctionSignature =
        FunctionSignature("re_entry_${entry.name}", VoidType, emptyList())

    override fun lower(node: EntryDeclaration): FunctionAction =
        FunctionAction(signatureOf(node), blockStatementLowerer.get().lower(node.body).body)
}
