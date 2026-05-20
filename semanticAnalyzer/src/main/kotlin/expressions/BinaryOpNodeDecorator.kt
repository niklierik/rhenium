package me.eriknikli.rhenium.semanticAnalyzer.expressions

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.expressions.operators.BinaryOpExpression
import me.eriknikli.rhenium.ast.tree.expressions.operators.Operator
import me.eriknikli.rhenium.semanticContext.scope.types.*
import javax.inject.Inject
import javax.inject.Singleton

interface IBinaryOpNodeDecorator {
    fun decorate(
        binaryOpExpression: BinaryOpExpression,
        expressionNodeDecoratorContext: ExpressionNodeDecoratorContext
    )
}

@Singleton
class BinaryOpNodeDecorator
@Inject
constructor() : IBinaryOpNodeDecorator {
    @Inject
    lateinit var expressionNodeDecoratorProvider: Lazy<IExpressionNodeDecorator>

    private val expressionNodeDecorator by lazy { expressionNodeDecoratorProvider.get() }

    override fun decorate(
        binaryOpExpression: BinaryOpExpression,
        expressionNodeDecoratorContext: ExpressionNodeDecoratorContext
    ) {
        val scope = expressionNodeDecoratorContext.scope

        val left = binaryOpExpression.left
        val right = binaryOpExpression.right

        expressionNodeDecorator.decorateExpression(left, ExpressionNodeDecoratorContext(scope))
        expressionNodeDecorator.decorateExpression(right, ExpressionNodeDecoratorContext(scope))

        binaryOpExpression.context.type =
            resolveType(left.context.type, right.context.type, binaryOpExpression.operator)
    }

    private fun resolveType(left: ExpressionType, right: ExpressionType, operator: Operator): ExpressionType {
        if (left.isNumeric() && right.isNumeric()) {
            when (operator) {
                Operator.HAT -> {
                    if (left !is FloatType && right !is FloatType) {
                        return InvalidType()
                    }

                    if (left == FloatType.F32 && right == FloatType.F32) {
                        return FloatType.F32
                    }

                    return FloatType.F64
                }

                Operator.STAR, Operator.SLASH, Operator.PERCENT, Operator.PLUS, Operator.MINUS -> {
                    if (left is SignedIntType && right is SignedIntType) {
                        return if (left.index > right.index) right else left
                    }
                    if (left is UnsignedIntType && right is UnsignedIntType) {
                        return if (left.index > right.index) right else left
                    }
                    if (left is FloatType && right is FloatType) {
                        return if (left.index > right.index) right else left
                    }

                    if (left is FloatType) {
                        return left
                    }

                    if (right is FloatType) {
                        return right
                    }

                    if (left is SignedIntType) {
                        return left
                    }

                    if (right is SignedIntType) {
                        return right
                    }

                    throw IllegalStateException("Unhandled numeric binary op type case: left = ${left}, right = ${right}, op = ${operator}.")
                }

                Operator.EQUALS, Operator.NOT_EQUALS -> {
                    return BooleanType()
                }

                Operator.GREATER,
                Operator.GREATER_EQUALS,
                Operator.LESS,
                Operator.LESS_EQUALS -> {
                    if (!left.isNumeric() || !right.isNumeric()) {
                        throw Exception("Operator $operator is expected to be used with numeric expressions.")
                    }

                    return BooleanType()
                }

                else -> throw IllegalStateException("Unhandled binary operator: ${operator}.")
            }
        }

        return InvalidType()
    }
}