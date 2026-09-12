package me.eriknikli.rhenium.transpiler

import dagger.Binds
import dagger.Module
import me.eriknikli.rhenium.transpiler.actions.*

@Module
interface CTranspilerModule {
    @Binds
    fun bindTranspiler(cTranspiler: CTranspiler): ITranspiler

    @Binds
    fun bindAnyActionTranspiler(transpiler: CAnyActionTranspiler): IAnyActionTranspiler

    @Binds
    fun bindBlockTranspiler(transpiler: CBlockTranspiler): IBlockTranspiler

    @Binds
    fun bindFunctionTranspiler(transpiler: CFunctionTranspiler): IFunctionTranspiler

    @Binds
    fun bindReturnTranspiler(transpiler: CReturnTranspiler): IReturnTranspiler

    @Binds
    fun bindPrintTranspiler(transpiler: CPrintTranspiler): IPrintTranspiler

    @Binds
    fun bindCastTranspiler(transpiler: CCastTranspiler): ICastTranspiler

    @Binds
    fun bindTernaryTranspiler(transpiler: CTernaryTranspiler): ITernaryTranspiler

    @Binds
    fun bindBinaryTranspiler(transpiler: CBinaryTranspiler): IBinaryTranspiler

    @Binds
    fun bindUnaryTranspiler(transpiler: CUnaryTranspiler): IUnaryTranspiler

    @Binds
    fun bindVarDeclarationTranspiler(transpiler: CVarDeclarationTranspiler): IVarDeclarationTranspiler

    @Binds
    fun bindAssignmentTranspiler(transpiler: CAssignmentTranspiler): IAssignmentTranspiler

    @Binds
    fun bindExpressionStatementTranspiler(transpiler: CExpressionStatementTranspiler): IExpressionStatementTranspiler

    @Binds
    fun bindVarRefTranspiler(transpiler: CVarRefTranspiler): IVarRefTranspiler

    @Binds
    fun bindConstantTranspiler(transpiler: CConstantTranspiler): IConstantTranspiler
}
