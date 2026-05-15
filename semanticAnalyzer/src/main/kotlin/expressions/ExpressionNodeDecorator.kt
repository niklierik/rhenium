package me.eriknikli.rhenium.semanticAnalyzer.expressions

import me.eriknikli.rhenium.ast.tree.expressions.Expression
import me.eriknikli.rhenium.ast.tree.expressions.literals.Literal
import javax.inject.Inject
import javax.inject.Singleton

interface IExpressionNodeDecorator {
    fun decorateExpression(expression: Expression)
}

@Singleton
class ExpressionNodeDecorator
@Inject
constructor() : IExpressionNodeDecorator {
    @Inject
    lateinit var literalNodeDecoratorProvider: dagger.Lazy<ILiteralNodeDecorator>

    private val literalNodeDecorator by lazy { literalNodeDecoratorProvider.get() }

    override fun decorateExpression(expression: Expression) {
        when (expression) {
            is Literal<*> -> literalNodeDecorator.decorateLiteral(expression)
        }
    }
}