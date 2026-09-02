package me.eriknikli.rhenium.transpiler

import dagger.Binds
import dagger.Module
import me.eriknikli.rhenium.transpiler.tree.CRootTranspiler
import me.eriknikli.rhenium.transpiler.tree.IRootTranspiler
import me.eriknikli.rhenium.transpiler.tree.expressions.*
import me.eriknikli.rhenium.transpiler.tree.statements.*

@Module
interface CTranspilerModule {
    @Binds
    fun bindTranspiler(cTranspiler: CTranspiler): ITranspiler

    @Binds
    fun bindRoot(rootTranspiler: CRootTranspiler): IRootTranspiler

    @Binds
    fun bindVarDecl(varDeclarationStatement: CVarDeclarationTranspiler): IVarDeclarationTranspiler

    @Binds
    fun bindVarAssignment(varAssignmentStatement: CVarAssignmentTranspiler): IVarAssignmentTranspiler

    @Binds
    fun bindLeftValue(leftValue: CLeftValueTranspiler): ILeftValueTranspiler

    @Binds
    fun bindStatement(statement: CStatementTranspiler): IStatementTranspiler

    @Binds
    fun bindExpressionStatement(expressionStatement: CExpressionStatementTranspiler): IExpressionStatementTranspiler

    @Binds
    fun bindExpression(expression: CExpressionTranspiler): IExpressionTranspiler

    @Binds
    fun bindLiteral(statement: CLiteralExpressionTranspiler): ILiteralExpressionTranspiler

    @Binds
    fun bindBinary(statement: CBinaryOpTranspiler): IBinaryOpTranspiler

    @Binds
    fun bindUnary(statement: CUnaryOpTranspiler): IUnaryOpTranspiler
}