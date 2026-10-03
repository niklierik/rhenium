package me.eriknikli.rhenium.semanticAnalyzer.statements

import arrow.core.raise.either
import arrow.core.nel
import arrow.core.raise.ensure
import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.expressions.LeftValue
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.ImmutableLeftValue
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.NotAnLValue
import me.eriknikli.rhenium.semanticAnalyzer.expressions.ExpressionNodeDecoratorContext
import me.eriknikli.rhenium.semanticAnalyzer.expressions.IExpressionNodeDecorator
import me.eriknikli.rhenium.semanticContext.scope.Scope
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import me.eriknikli.rhenium.semanticContext.tree.expressions.LeftValueContext
import javax.inject.Inject
import javax.inject.Singleton

interface IMutableLeftValueDecorator {
    fun decorate(leftValue: LeftValue, scope: Scope): Diagnosed<ExpressionType>
}

@Singleton
class MutableLeftValueDecorator
@Inject
constructor() : IMutableLeftValueDecorator {
    @Inject
    lateinit var expressionNodeDecoratorProvider: Lazy<IExpressionNodeDecorator>

    private val expressionNodeDecorator by lazy { expressionNodeDecoratorProvider.get() }

    override fun decorate(leftValue: LeftValue, scope: Scope): Diagnosed<ExpressionType> = either {
        expressionNodeDecorator
            .decorateExpression(leftValue, ExpressionNodeDecoratorContext(scope))
            .bind()

        val leftValueContext = leftValue.context
        if (leftValueContext !is LeftValueContext) {
            raise(NotAnLValue(leftValue.parserContext).nel())
        }
        ensure(leftValueContext.symbol.mutable) {
            ImmutableLeftValue(leftValue.parserContext, leftValue).nel()
        }

        leftValueContext.type
    }
}
