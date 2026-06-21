package me.eriknikli.rhenium.semanticAnalyzer.expressions

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.expressions.Expression
import me.eriknikli.rhenium.ast.tree.expressions.Identifier
import me.eriknikli.rhenium.ast.tree.expressions.literals.Literal
import me.eriknikli.rhenium.ast.tree.expressions.operators.BinaryOpExpression
import me.eriknikli.rhenium.ast.tree.expressions.operators.UnaryOpExpression
import me.eriknikli.rhenium.semanticAnalyzer.exceptions.IllegalExpressionException
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

    @Inject
    lateinit var binaryNodeDecoratorProvider: dagger.Lazy<IBinaryOpNodeDecorator>

    private val binaryNodeDecorator by lazy { binaryNodeDecoratorProvider.get() }

    @Inject
    lateinit var unaryNodeDecoratorProvider: dagger.Lazy<IUnaryOpNodeDecorator>

    private val unaryNodeDecorator by lazy { unaryNodeDecoratorProvider.get() }

    @Inject
    lateinit var identifierNodeDecorator: Lazy<IIdentifierNodeDecorator>

    override fun decorateExpression(
        expression: Expression,
        expressionNodeDecoratorContext: ExpressionNodeDecoratorContext
    ) {
        expression.context.relevantScope = expressionNodeDecoratorContext.scope
        when (expression) {
            is BinaryOpExpression -> binaryNodeDecorator.decorate(expression, expressionNodeDecoratorContext)
            is UnaryOpExpression -> unaryNodeDecorator.decorate(expression, expressionNodeDecoratorContext)
            is Literal<*> -> literalNodeDecorator.decorateLiteral(expression)
            is Identifier -> identifierNodeDecorator.get()
                .decorateIdentifier(expression, expressionNodeDecoratorContext)

            else -> throw IllegalExpressionException(expression.parserContext, expression.javaClass)
        }
    }
}