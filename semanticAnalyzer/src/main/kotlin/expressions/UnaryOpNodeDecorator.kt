package me.eriknikli.rhenium.semanticAnalyzer.expressions

import arrow.core.leftNel
import arrow.core.raise.either
import arrow.core.right
import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.expressions.operators.Operator
import me.eriknikli.rhenium.ast.tree.expressions.operators.UnaryOpExpression
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.IllegalUnaryOperation
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.UnaryOperatorTypeMismatch
import me.eriknikli.rhenium.semanticContext.scope.types.BooleanType
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import me.eriknikli.rhenium.semanticContext.scope.types.InvalidType
import me.eriknikli.rhenium.semanticContext.scope.types.UnsignedIntType
import me.eriknikli.rhenium.semanticContext.scope.types.isNumeric
import me.eriknikli.rhenium.semanticContext.scope.types.numericTypes
import javax.inject.Inject
import javax.inject.Singleton

interface IUnaryOpNodeDecorator {
    fun decorate(
        node: UnaryOpExpression,
        context: ExpressionNodeDecoratorContext
    ): Diagnosed<ExpressionType>
}

@Singleton
class UnaryOpNodeDecorator
@Inject
constructor() : IUnaryOpNodeDecorator {
    @Inject
    lateinit var expressionNodeDecoratorProvider: Lazy<IExpressionNodeDecorator>

    private val expressionNodeDecorator: IExpressionNodeDecorator by lazy { expressionNodeDecoratorProvider.get() }

    override fun decorate(
        node: UnaryOpExpression,
        context: ExpressionNodeDecoratorContext
    ): Diagnosed<ExpressionType> = either {
        val expression = node.expression
        val inputType = expressionNodeDecorator
            .decorateExpression(expression, ExpressionNodeDecoratorContext(context.scope))
            .bind()

        val type = resolveType(inputType, node.operator, node).bind()

        node.context.type = type

        type
    }

    private fun resolveType(
        inputType: ExpressionType,
        operator: Operator,
        node: UnaryOpExpression
    ): Diagnosed<ExpressionType> {
        if (inputType is InvalidType) {
            return InvalidType.right()
        }

        val parserContext = node.expression.parserContext

        return when (operator) {
            Operator.BANG ->
                if (inputType is BooleanType) {
                    inputType.right()
                } else {
                    UnaryOperatorTypeMismatch(parserContext, operator, inputType, listOf(BooleanType)).leftNel()
                }

            Operator.PLUS ->
                if (inputType.isNumeric()) {
                    inputType.right()
                } else {
                    UnaryOperatorTypeMismatch(
                        parserContext,
                        operator,
                        inputType,
                        numericTypes().toList()
                    ).leftNel()
                }

            Operator.MINUS ->
                if (inputType.isNumeric() && inputType !is UnsignedIntType) {
                    inputType.right()
                } else {
                    UnaryOperatorTypeMismatch(
                        parserContext,
                        operator,
                        inputType,
                        numericTypes().minus(UnsignedIntType.entries.toSet()).toList()
                    ).leftNel()
                }

            else -> IllegalUnaryOperation(parserContext, inputType, operator).leftNel()
        }
    }
}
