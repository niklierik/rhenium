package me.eriknikli.rhenium.semanticAnalyzer.expressions

import me.eriknikli.rhenium.ast.tree.expressions.Expression
import me.eriknikli.rhenium.ast.tree.expressions.literals.Literal
import me.eriknikli.rhenium.semanticContext.scope.Scope
import javax.inject.Inject
import javax.inject.Singleton

data class ExpressionNodeDecoratorContext(val scope: Scope)

interface IExpressionNodeDecorator {
    fun decorateExpression(
        expression: Expression,
        expressionNodeDecoratorContext: ExpressionNodeDecoratorContext
    )
}

@Singleton
class ExpressionNodeDecorator
@Inject
constructor() : IExpressionNodeDecorator {
    @Inject
    lateinit var literalNodeDecoratorProvider: dagger.Lazy<ILiteralNodeDecorator>

    private val literalNodeDecorator by lazy { literalNodeDecoratorProvider.get() }

    override fun decorateExpression(
        expression: Expression,
        expressionNodeDecoratorContext: ExpressionNodeDecoratorContext
    ) {
        when (expression) {
            is Literal<*> -> literalNodeDecorator.decorateLiteral(expression)
        }
    }
}