package me.eriknikli.rhenium.semanticAnalyzer.expressions

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.expressions.operators.Operator
import me.eriknikli.rhenium.ast.tree.expressions.operators.UnaryOpExpression
import me.eriknikli.rhenium.semanticContext.scope.types.BooleanType
import me.eriknikli.rhenium.semanticContext.scope.types.UnsignedIntType
import me.eriknikli.rhenium.semanticContext.scope.types.isNumeric
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
        expressionNodeDecorator.decorateExpression(expression, ExpressionNodeDecoratorContext(scope))

        val operator = node.operator
        val inputType = expression.context.type

        when (operator) {
            Operator.BANG -> {
                if (node.context.type !is BooleanType) {
                    throw Exception("Operator '!' as an unary expression is expected to be used with boolean expressions.")
                }
                node.context.relevantScope = scope
                node.context.type = inputType
                return
            }

            Operator.PLUS -> {
                if (!inputType.isNumeric()) {
                    throw Exception("Operator '+' as an unary expression is expected to be used with numeric expressions.")
                }

                node.context.relevantScope = scope
                node.context.type = inputType
                return
            }

            Operator.MINUS -> {
                if (!inputType.isNumeric()) {
                    throw Exception("Operator '-' as an unary expression is expected to be used with signed integer or float expressions.")
                }
                if (inputType is UnsignedIntType) {
                    throw Exception("Operator '-' as an unary expression is expected to be used with signed integer or float expressions.")
                }

                node.context.type = inputType
                node.context.relevantScope = scope
            }

            else -> throw IllegalStateException("Invalid unary operator: ${operator}.")
        }
    }
}