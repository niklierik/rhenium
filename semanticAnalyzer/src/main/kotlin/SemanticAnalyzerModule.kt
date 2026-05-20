package me.eriknikli.rhenium.semanticAnalyzer

import dagger.Binds
import dagger.Module
import me.eriknikli.rhenium.semanticAnalyzer.expressions.*
import me.eriknikli.rhenium.semanticAnalyzer.statements.IStatementNodeDecorator
import me.eriknikli.rhenium.semanticAnalyzer.statements.IVarDeclarationStatementDecorator
import me.eriknikli.rhenium.semanticAnalyzer.statements.StatementNodeDecorator
import me.eriknikli.rhenium.semanticAnalyzer.statements.VarDeclarationStatementDecorator

@Module
interface SemanticAnalyzerModule {
    @Binds
    fun bindSemanticAnalyzer(instance: SemanticAnalyzer): ISemanticAnalyzer

    @Binds
    fun bindRootNodeDecorator(instance: RootNodeDecorator): IRootNodeDecorator

    @Binds
    fun bindLiteralNodeDecorator(instance: LiteralNodeDecorator): ILiteralNodeDecorator

    @Binds
    fun bindStatementNodeDecorator(instance: StatementNodeDecorator): IStatementNodeDecorator

    @Binds
    fun bindExpressionNodeDecorator(instance: ExpressionNodeDecorator): IExpressionNodeDecorator

    @Binds
    fun bindBinaryOpDecorator(instance: BinaryOpNodeDecorator): IBinaryOpNodeDecorator

    @Binds
    fun bindVarDeclarationDecorator(instance: VarDeclarationStatementDecorator): IVarDeclarationStatementDecorator

    @Binds
    fun bindUnaryOpDecorator(instance: UnaryOpNodeDecorator): IUnaryOpNodeDecorator
}