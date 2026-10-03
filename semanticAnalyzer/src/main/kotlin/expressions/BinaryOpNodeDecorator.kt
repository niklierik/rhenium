package me.eriknikli.rhenium.semanticAnalyzer.expressions

import arrow.core.nel
import arrow.core.raise.either
import arrow.core.raise.zipOrAccumulate
import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.expressions.operators.BinaryOpExpression
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.common.diagnostics.Diagnostic
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.IllegalBinaryOperation
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.MixedSignedness
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
            .mapLeft { failure -> failure.toDiagnostic(leftType, rightType, binaryOpExpression).nel() }
            .bind()

        binaryOpExpression.context.type = type

        type
    }

    private fun BinaryOperatorFailure.toDiagnostic(
        left: ExpressionType,
        right: ExpressionType,
        expression: BinaryOpExpression
    ): Diagnostic = when (this) {
        BinaryOperatorFailure.ILLEGAL_OPERATION ->
            IllegalBinaryOperation(expression.parserContext, left, right, expression.operator)

        BinaryOperatorFailure.MIXED_SIGNEDNESS ->
            MixedSignedness(expression.parserContext, left, right, expression.operator.cString)
    }
}
