package me.eriknikli.rhenium.semanticAnalyzer.statements

import arrow.core.getOrElse
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.zipOrAccumulate
import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.expressions.Expression
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.TypeMismatch
import me.eriknikli.rhenium.semanticAnalyzer.expressions.ExpressionNodeDecoratorContext
import me.eriknikli.rhenium.semanticAnalyzer.expressions.IExpressionNodeDecorator
import me.eriknikli.rhenium.semanticContext.scope.types.BooleanType
import me.eriknikli.rhenium.semanticContext.scope.types.InvalidType
import javax.inject.Inject
import javax.inject.Singleton

interface IConditionDecorator {
    fun decorate(condition: Expression, context: StatementDecoratorContext): Diagnosed<Unit>
}

@Singleton
class ConditionDecorator
@Inject
constructor() : IConditionDecorator {
    @Inject
    lateinit var expressionNodeDecoratorProvider: Lazy<IExpressionNodeDecorator>

    private val expressionNodeDecorator by lazy { expressionNodeDecoratorProvider.get() }

    override fun decorate(
        condition: Expression,
        context: StatementDecoratorContext
    ): Diagnosed<Unit> = either {
        val decorated = expressionNodeDecorator
            .decorateExpression(condition, ExpressionNodeDecoratorContext(context.scope))

        val conditionType = decorated.getOrElse { InvalidType }

        zipOrAccumulate(
            { decorated.bindNel() },
            {
                ensure(conditionType is BooleanType) {
                    TypeMismatch(condition.parserContext, conditionType, listOf(BooleanType))
                }
            }
        ) { _, _ -> }
    }
}
