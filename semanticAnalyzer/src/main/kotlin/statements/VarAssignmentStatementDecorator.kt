package me.eriknikli.rhenium.semanticAnalyzer.statements

import arrow.core.nel
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.zipOrAccumulate
import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.statements.vars.VarAssignmentStatement
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.ImmutableLeftValue
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.NotAnLValue
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.TypeMismatch
import me.eriknikli.rhenium.semanticAnalyzer.expressions.ExpressionNodeDecoratorContext
import me.eriknikli.rhenium.semanticAnalyzer.expressions.IExpressionNodeDecorator
import me.eriknikli.rhenium.semanticContext.tree.expressions.LeftValueContext
import javax.inject.Inject
import javax.inject.Singleton

interface IVarAssignmentStatementDecorator {
    fun decorate(statement: VarAssignmentStatement, context: StatementDecoratorContext): Diagnosed<Unit>
}

@Singleton
class VarAssignmentStatementDecorator
@Inject
constructor() : IVarAssignmentStatementDecorator {
    @Inject
    lateinit var expressionNodeDecoratorProvider: Lazy<IExpressionNodeDecorator>

    private val expressionNodeDecorator by lazy { expressionNodeDecoratorProvider.get() }

    override fun decorate(
        statement: VarAssignmentStatement,
        context: StatementDecoratorContext
    ): Diagnosed<Unit> = either {
        val scope = context.scope
        statement.context.relevantScope = scope

        val (targetType, valueType) = zipOrAccumulate(
            {
                val leftValue = statement.leftValue
                expressionNodeDecorator
                    .decorateExpression(leftValue, ExpressionNodeDecoratorContext(scope))
                    .bindNel()

                val leftValueContext = leftValue.context
                if (leftValueContext !is LeftValueContext) {
                    raise(NotAnLValue(leftValue.parserContext))
                }
                ensure(leftValueContext.symbol.mutable) {
                    ImmutableLeftValue(leftValue.parserContext, leftValue)
                }

                leftValueContext.type
            },
            {
                expressionNodeDecorator
                    .decorateExpression(statement.rightValue, ExpressionNodeDecoratorContext(scope))
                    .bindNel()
            }
        ) { target, value -> target to value }

        ensure(valueType.canAssignTo(targetType)) {
            TypeMismatch(statement.parserContext, valueType, listOf(targetType)).nel()
        }
    }
}
