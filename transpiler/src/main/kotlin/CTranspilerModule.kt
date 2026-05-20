package me.eriknikli.rhenium.transpiler

import dagger.Binds
import dagger.Module
import me.eriknikli.rhenium.transpiler.tree.CRootTranspiler
import me.eriknikli.rhenium.transpiler.tree.IRootTranspiler
import me.eriknikli.rhenium.transpiler.tree.expressions.*
import me.eriknikli.rhenium.transpiler.tree.statements.CStatementTranspiler
import me.eriknikli.rhenium.transpiler.tree.statements.CVarDeclarationTranspiler
import me.eriknikli.rhenium.transpiler.tree.statements.IStatementTranspiler
import me.eriknikli.rhenium.transpiler.tree.statements.IVarDeclarationTranspiler

@Module
interface CTranspilerModule {
    @Binds
    fun bindTranspiler(cTranspiler: CTranspiler): ITranspiler

    @Binds
    fun bindRoot(rootTranspiler: CRootTranspiler): IRootTranspiler

    @Binds
    fun bindVarDecl(varDeclarationStatement: CVarDeclarationTranspiler): IVarDeclarationTranspiler

    @Binds
    fun bindStatement(statement: CStatementTranspiler): IStatementTranspiler

    @Binds
    fun bindExpression(expression: CExpressionTranspiler): IExpressionTranspiler

    @Binds
    fun bindLiteral(statement: CLiteralExpressionTranspiler): ILiteralExpressionTranspiler

    @Binds
    fun bindBinary(statement: CBinaryOpTranspiler): IBinaryOpTranspiler

    @Binds
    fun bindUnary(statement: CUnaryOpTranspiler): IUnaryOpTranspiler
}