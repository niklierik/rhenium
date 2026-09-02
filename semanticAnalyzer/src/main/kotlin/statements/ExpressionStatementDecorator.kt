package me.eriknikli.rhenium.semanticAnalyzer.statements

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.statements.ExpressionStatement
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.semanticAnalyzer.expressions.ExpressionNodeDecoratorContext
import me.eriknikli.rhenium.semanticAnalyzer.expressions.IExpressionNodeDecorator
import javax.inject.Inject
import javax.inject.Singleton

interface IExpressionStatementDecorator {
    fun decorate(statement: ExpressionStatement, context: StatementDecoratorContext): Diagnosed<Unit>
}

@Singleton
class ExpressionStatementDecorator
@Inject
constructor() : IExpressionStatementDecorator {
    @Inject
    lateinit var expressionNodeDecoratorProvider: Lazy<IExpressionNodeDecorator>

    private val expressionNodeDecorator by lazy { expressionNodeDecoratorProvider.get() }

    override fun decorate(
        statement: ExpressionStatement,
        context: StatementDecoratorContext
    ): Diagnosed<Unit> {
        val scope = context.scope
        statement.context.relevantScope = scope

        return expressionNodeDecorator
            .decorateExpression(statement.expression, ExpressionNodeDecoratorContext(scope))
            .map { }
    }
}
