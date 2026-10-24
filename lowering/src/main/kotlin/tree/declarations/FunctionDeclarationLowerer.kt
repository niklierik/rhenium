package me.eriknikli.rhenium.lowering.tree.declarations

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.FunctionDeclaration
import me.eriknikli.rhenium.lowering.INodeLowerer
import me.eriknikli.rhenium.lowering.actions.FunctionAction
import me.eriknikli.rhenium.lowering.actions.FunctionParameter
import me.eriknikli.rhenium.lowering.actions.FunctionSignature
import me.eriknikli.rhenium.lowering.tree.statements.IBlockStatementLowerer
import javax.inject.Inject
import javax.inject.Singleton

interface IFunctionDeclarationLowerer : INodeLowerer<FunctionDeclaration, FunctionAction> {
    fun signatureOf(function: FunctionDeclaration): FunctionSignature
}

@Singleton
class FunctionDeclarationLowerer
@Inject
constructor() : IFunctionDeclarationLowerer {
    @Inject
    lateinit var blockStatementLowerer: Lazy<IBlockStatementLowerer>

    override fun signatureOf(function: FunctionDeclaration): FunctionSignature {
        val symbol = function.context.symbol

        return FunctionSignature(
            symbol.cName,
            symbol.signature.returnType,
            function.parameters.map {
                val variable = it.context.variable
                FunctionParameter(variable.declaredType, variable.cName)
            }
        )
    }

    override fun lower(node: FunctionDeclaration): FunctionAction =
        FunctionAction(signatureOf(node), blockStatementLowerer.get().lower(node.body).body)
}
