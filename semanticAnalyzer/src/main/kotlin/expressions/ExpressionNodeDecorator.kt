package me.eriknikli.rhenium.semanticAnalyzer.expressions

import arrow.core.right
import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.expressions.Expression
import me.eriknikli.rhenium.ast.tree.expressions.Identifier
import me.eriknikli.rhenium.ast.tree.expressions.literals.Literal
import me.eriknikli.rhenium.ast.tree.expressions.operators.BinaryOpExpression
import me.eriknikli.rhenium.ast.tree.expressions.operators.UnaryOpExpression
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.semanticContext.scope.Scope
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import javax.inject.Inject
import javax.inject.Singleton

data class ExpressionNodeDecoratorContext(val scope: Scope)

interface IExpressionNodeDecorator {
    fun decorateExpression(
        expression: Expression,
        expressionNodeDecoratorContext: ExpressionNodeDecoratorContext
    ): Diagnosed<ExpressionType>
}

@Singleton
class ExpressionNodeDecorator
@Inject
constructor() : IExpressionNodeDecorator {
    @Inject
    lateinit var literalNodeDecoratorProvider: Lazy<ILiteralNodeDecorator>

    private val literalNodeDecorator by lazy { literalNodeDecoratorProvider.get() }

    @Inject
    lateinit var binaryNodeDecoratorProvider: Lazy<IBinaryOpNodeDecorator>

    private val binaryNodeDecorator by lazy { binaryNodeDecoratorProvider.get() }

    @Inject
    lateinit var unaryNodeDecoratorProvider: Lazy<IUnaryOpNodeDecorator>

    private val unaryNodeDecorator by lazy { unaryNodeDecoratorProvider.get() }

    @Inject
    lateinit var identifierNodeDecorator: Lazy<IIdentifierNodeDecorator>

    override fun decorateExpression(
        expression: Expression,
        expressionNodeDecoratorContext: ExpressionNodeDecoratorContext
    ): Diagnosed<ExpressionType> {
        expression.context.relevantScope = expressionNodeDecoratorContext.scope

        return when (expression) {
            is BinaryOpExpression -> binaryNodeDecorator.decorate(expression, expressionNodeDecoratorContext)
            is UnaryOpExpression -> unaryNodeDecorator.decorate(expression, expressionNodeDecoratorContext)
            is Literal<*> -> literalNodeDecorator.decorateLiteral(expression).right()
            is Identifier -> identifierNodeDecorator.get()
                .decorateIdentifier(expression, expressionNodeDecoratorContext)

            else -> throw IllegalStateException(
                "Unhandled expression node ${expression.javaClass.simpleName} cannot be decorated."
            )
        }
    }
}
