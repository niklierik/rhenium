package me.eriknikli.rhenium.semanticAnalyzer.statements

import arrow.core.right
import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.statements.PrintStatement
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.semanticAnalyzer.expressions.ExpressionNodeDecoratorContext
import me.eriknikli.rhenium.semanticAnalyzer.expressions.IExpressionNodeDecorator
import javax.inject.Inject
import javax.inject.Singleton

interface IPrintStatementDecorator {
    fun decorate(statement: PrintStatement, context: StatementDecoratorContext): Diagnosed<Unit>
}

@Singleton
class PrintStatementDecorator
@Inject
constructor() : IPrintStatementDecorator {
    @Inject
    lateinit var expressionNodeDecoratorProvider: Lazy<IExpressionNodeDecorator>

    private val expressionNodeDecorator by lazy { expressionNodeDecoratorProvider.get() }

    override fun decorate(
        statement: PrintStatement,
        context: StatementDecoratorContext
    ): Diagnosed<Unit> {
        val scope = context.scope
        statement.context.relevantScope = scope

        val expression = statement.expression ?: return Unit.right()

        return expressionNodeDecorator
            .decorateExpression(expression, ExpressionNodeDecoratorContext(scope))
            .map { }
    }
}
