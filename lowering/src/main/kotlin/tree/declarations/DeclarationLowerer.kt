package me.eriknikli.rhenium.lowering.tree.declarations

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.Declaration
import me.eriknikli.rhenium.ast.tree.EntryDeclaration
import me.eriknikli.rhenium.ast.tree.FunctionDeclaration
import me.eriknikli.rhenium.lowering.INodeLowerer
import me.eriknikli.rhenium.lowering.actions.FunctionAction
import me.eriknikli.rhenium.lowering.actions.FunctionParameter
import me.eriknikli.rhenium.lowering.actions.FunctionSignature
import me.eriknikli.rhenium.lowering.tree.statements.IBlockStatementLowerer
import me.eriknikli.rhenium.semanticContext.scope.types.VoidType
import javax.inject.Inject
import javax.inject.Singleton

interface IDeclarationLowerer : INodeLowerer<Declaration, FunctionAction> {
    fun signatureOf(declaration: Declaration): FunctionSignature
}

@Singleton
class DeclarationLowerer
@Inject
constructor() : IDeclarationLowerer {
    @Inject
    lateinit var blockStatementLowererProvider: Lazy<IBlockStatementLowerer>

    private val blockStatementLowerer by lazy { blockStatementLowererProvider.get() }

    override fun signatureOf(declaration: Declaration): FunctionSignature = when (declaration) {
        is EntryDeclaration -> FunctionSignature("re_entry_${declaration.name}", VoidType, emptyList())
        is FunctionDeclaration -> {
            val symbol = declaration.context.symbol

            FunctionSignature(
                symbol.cName,
                symbol.signature.returnType,
                declaration.parameters.map {
                    val variable = it.context.variable
                    FunctionParameter(variable.declaredType, variable.cName)
                }
            )
        }
    }

    override fun lower(node: Declaration): FunctionAction =
        FunctionAction(signatureOf(node), blockStatementLowerer.lower(node.body).body)
}
