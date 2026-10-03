package me.eriknikli.rhenium.semanticAnalyzer.expressions

import arrow.core.nel
import arrow.core.raise.either
import arrow.core.raise.zipOrAccumulate
import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.expressions.operators.BinaryOpExpression
import me.eriknikli.rhenium.ast.tree.expressions.operators.WrittenOperator
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import javax.inject.Inject
import javax.inject.Singleton

interface IBinaryOpNodeDecorator {
    fun decorate(
        binaryOpExpression: BinaryOpExpression,
        expressionNodeDecoratorContext: ExpressionNodeDecoratorContext
    ): Diagnosed<ExpressionType>
}

@Singleton
class BinaryOpNodeDecorator
@Inject
constructor() : IBinaryOpNodeDecorator {
    @Inject
    lateinit var expressionNodeDecoratorProvider: Lazy<IExpressionNodeDecorator>

    @Inject
    lateinit var binaryOperatorTypeRule: IBinaryOperatorTypeRule

    private val expressionNodeDecorator by lazy { expressionNodeDecoratorProvider.get() }

    override fun decorate(
        binaryOpExpression: BinaryOpExpression,
        expressionNodeDecoratorContext: ExpressionNodeDecoratorContext
    ): Diagnosed<ExpressionType> = either {
        val scope = expressionNodeDecoratorContext.scope

        val (leftType, rightType) = zipOrAccumulate(
            {
                expressionNodeDecorator
                    .decorateExpression(binaryOpExpression.left, ExpressionNodeDecoratorContext(scope))
                    .bindNel()
            },
            {
                expressionNodeDecorator
                    .decorateExpression(binaryOpExpression.right, ExpressionNodeDecoratorContext(scope))
                    .bindNel()
            }
        ) { left, right -> left to right }

        val type = binaryOperatorTypeRule
            .resolve(leftType, rightType, binaryOpExpression.operator)
            .mapLeft { failure ->
                failure.toDiagnostic(
                    binaryOpExpression.parserContext,
                    leftType,
                    rightType,
                    WrittenOperator.Plain(binaryOpExpression.operator)
                ).nel()
            }
            .bind()

        binaryOpExpression.context.type = type

        type
    }
}
