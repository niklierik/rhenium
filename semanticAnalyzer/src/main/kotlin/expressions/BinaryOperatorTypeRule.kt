package me.eriknikli.rhenium.semanticAnalyzer.expressions

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import me.eriknikli.rhenium.ast.tree.expressions.operators.Operator
import me.eriknikli.rhenium.semanticContext.scope.types.*
import javax.inject.Inject
import javax.inject.Singleton

interface IBinaryOperatorTypeRule {
    fun resolve(
        left: ExpressionType,
        right: ExpressionType,
        operator: Operator
    ): Either<BinaryOperatorFailure, ExpressionType>
}

@Singleton
class BinaryOperatorTypeRule
@Inject
constructor() : IBinaryOperatorTypeRule {
    override fun resolve(
        left: ExpressionType,
        right: ExpressionType,
        operator: Operator
    ): Either<BinaryOperatorFailure, ExpressionType> {
        if (left is InvalidType || right is InvalidType) {
            return InvalidType.right()
        }

        if (left == BooleanType && right == BooleanType) {
            return when (operator) {
                Operator.AND, Operator.OR, Operator.EQUALS, Operator.NOT_EQUALS -> BooleanType.right()
                else -> BinaryOperatorFailure.ILLEGAL_OPERATION.left()
            }
        }

        if (!left.isNumeric() || !right.isNumeric()) {
            return BinaryOperatorFailure.ILLEGAL_OPERATION.left()
        }

        if (isMixedSignedness(left, right)) {
            return BinaryOperatorFailure.MIXED_SIGNEDNESS.left()
        }

        return when (operator) {
            Operator.HAT -> {
                if (left !is FloatType && right !is FloatType) {
                    return BinaryOperatorFailure.ILLEGAL_OPERATION.left()
                }

                if (left == FloatType.F32 && right == FloatType.F32) {
                    FloatType.F32.right()
                } else {
                    FloatType.F64.right()
                }
            }

            Operator.STAR, Operator.SLASH, Operator.PERCENT, Operator.PLUS, Operator.MINUS -> {
                if (operator == Operator.PERCENT && (left is FloatType || right is FloatType)) {
                    return BinaryOperatorFailure.ILLEGAL_OPERATION.left()
                }

                arithmeticType(left, right)?.right() ?: BinaryOperatorFailure.ILLEGAL_OPERATION.left()
            }

            Operator.EQUALS, Operator.NOT_EQUALS -> BooleanType.right()

            Operator.GREATER,
            Operator.GREATER_EQUALS,
            Operator.LESS,
            Operator.LESS_EQUALS -> BooleanType.right()

            else -> BinaryOperatorFailure.ILLEGAL_OPERATION.left()
        }
    }

    private fun isMixedSignedness(left: ExpressionType, right: ExpressionType): Boolean =
        (left is SignedIntType && right is UnsignedIntType) ||
                (left is UnsignedIntType && right is SignedIntType)

    private fun arithmeticType(left: ExpressionType, right: ExpressionType): ExpressionType? {
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

        return null
    }
}
