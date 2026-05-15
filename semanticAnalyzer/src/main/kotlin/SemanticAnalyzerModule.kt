package me.eriknikli.rhenium.semanticAnalyzer

import dagger.Binds
import dagger.Module
import me.eriknikli.rhenium.semanticAnalyzer.expressions.ExpressionNodeDecorator
import me.eriknikli.rhenium.semanticAnalyzer.expressions.IExpressionNodeDecorator
import me.eriknikli.rhenium.semanticAnalyzer.expressions.ILiteralNodeDecorator
import me.eriknikli.rhenium.semanticAnalyzer.expressions.LiteralNodeDecorator
import me.eriknikli.rhenium.semanticAnalyzer.statements.IStatementNodeDecorator
import me.eriknikli.rhenium.semanticAnalyzer.statements.StatementNodeDecorator

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
}