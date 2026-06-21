package me.eriknikli.rhenium.semanticAnalyzer.expressions

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.expressions.operators.Operator
import me.eriknikli.rhenium.ast.tree.expressions.operators.UnaryOpExpression
import me.eriknikli.rhenium.semanticAnalyzer.exceptions.IllegalUnaryOperation
import me.eriknikli.rhenium.semanticAnalyzer.exceptions.UnaryOperatorTypeMismatchException
import me.eriknikli.rhenium.semanticContext.scope.types.BooleanType
import me.eriknikli.rhenium.semanticContext.scope.types.UnsignedIntType
import me.eriknikli.rhenium.semanticContext.scope.types.isNumeric
import me.eriknikli.rhenium.semanticContext.scope.types.numericTypes
import javax.inject.Inject
import javax.inject.Singleton

interface IUnaryOpNodeDecorator {
    fun decorate(node: UnaryOpExpression, context: ExpressionNodeDecoratorContext)
}

@Singleton
class UnaryOpNodeDecorator
@Inject
constructor(
) : IUnaryOpNodeDecorator {
    @Inject
    lateinit var expressionNodeDecoratorProvider: Lazy<IExpressionNodeDecorator>

    private val expressionNodeDecorator: IExpressionNodeDecorator by lazy { expressionNodeDecoratorProvider.get() }

    override fun decorate(node: UnaryOpExpression, context: ExpressionNodeDecoratorContext) {
        val scope = context.scope

        val expression = node.expression
        val operator = node.operator

        expressionNodeDecorator.decorateExpression(expression, ExpressionNodeDecoratorContext(scope))
        val inputType = expression.context.type

        when (operator) {
            Operator.BANG -> {
                if (inputType !is BooleanType) {
                    throw UnaryOperatorTypeMismatchException(
                        expression.parserContext,
                        operator,
                        inputType,
                        BooleanType()
                    )
                }
            }

            Operator.PLUS -> {
                if (!inputType.isNumeric()) {
                    throw UnaryOperatorTypeMismatchException(
                        expression.parserContext,
                        operator,
                        inputType,
                        *numericTypes().toTypedArray()
                    )
                }
            }

            Operator.MINUS -> {
                if (!inputType.isNumeric()) {
                    throw UnaryOperatorTypeMismatchException(
                        expression.parserContext,
                        operator,
                        inputType,
                        *numericTypes().minus(UnsignedIntType.entries).toTypedArray()
                    )
                }
                if (inputType is UnsignedIntType) {
                    throw UnaryOperatorTypeMismatchException(
                        expression.parserContext,
                        operator,
                        inputType,
                        *numericTypes().minus(UnsignedIntType.entries).toTypedArray()
                    )
                }
            }

            else -> throw IllegalUnaryOperation(expression.parserContext, inputType, operator)
        }

        node.context.type = inputType
    }
}