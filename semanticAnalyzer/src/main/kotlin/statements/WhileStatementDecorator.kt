package me.eriknikli.rhenium.semanticAnalyzer.statements

import arrow.core.getOrElse
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.zipOrAccumulate
import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.statements.WhileStatement
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.semanticAnalyzer.expressions.ExpressionNodeDecoratorContext
import me.eriknikli.rhenium.semanticAnalyzer.expressions.IExpressionNodeDecorator
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.TypeMismatch
import me.eriknikli.rhenium.semanticContext.scope.types.BooleanType
import me.eriknikli.rhenium.semanticContext.scope.types.InvalidType
import javax.inject.Inject
import javax.inject.Singleton

interface IWhileStatementDecorator {
    fun decorate(statement: WhileStatement, context: StatementDecoratorContext): Diagnosed<Unit>
}

@Singleton
class WhileStatementDecorator
@Inject
constructor() : IWhileStatementDecorator {
    @Inject
    lateinit var expressionNodeDecoratorProvider: Lazy<IExpressionNodeDecorator>

    @Inject
    lateinit var statementNodeDecoratorProvider: Lazy<IStatementNodeDecorator>

    private val expressionNodeDecorator by lazy { expressionNodeDecoratorProvider.get() }
    private val statementNodeDecorator by lazy { statementNodeDecoratorProvider.get() }

    override fun decorate(
        statement: WhileStatement,
        context: StatementDecoratorContext
    ): Diagnosed<Unit> = either {
        val condition = expressionNodeDecorator
            .decorateExpression(statement.condition, ExpressionNodeDecoratorContext(context.scope))

        val conditionType = condition.getOrElse { InvalidType }

        zipOrAccumulate(
            { condition.bindNel() },
            {
                ensure(conditionType is BooleanType) {
                    TypeMismatch(statement.condition.parserContext, conditionType, listOf(BooleanType))
                }
            },
            {
                statementNodeDecorator
                    .decorateStatement(statement.body, StatementDecoratorContext(context.scope))
                    .bindNel()
            }
        ) { _, _, _ -> }
    }
}
