package me.eriknikli.rhenium.semanticAnalyzer.statements

import arrow.core.nel
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.zipOrAccumulate
import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.expressions.operators.WrittenOperator
import me.eriknikli.rhenium.ast.tree.statements.vars.CompoundAssignmentStatement
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.TypeMismatch
import me.eriknikli.rhenium.semanticAnalyzer.expressions.ExpressionNodeDecoratorContext
import me.eriknikli.rhenium.semanticAnalyzer.expressions.IBinaryOperatorTypeRule
import me.eriknikli.rhenium.semanticAnalyzer.expressions.IExpressionNodeDecorator
import javax.inject.Inject
import javax.inject.Singleton

interface ICompoundAssignmentStatementDecorator {
    fun decorate(statement: CompoundAssignmentStatement, context: StatementDecoratorContext): Diagnosed<Unit>
}

@Singleton
class CompoundAssignmentStatementDecorator
@Inject
constructor() : ICompoundAssignmentStatementDecorator {
    @Inject
    lateinit var expressionNodeDecoratorProvider: Lazy<IExpressionNodeDecorator>

    private val expressionNodeDecorator by lazy { expressionNodeDecoratorProvider.get() }

    @Inject
    lateinit var mutableLeftValueDecoratorProvider: Lazy<IMutableLeftValueDecorator>

    private val mutableLeftValueDecorator by lazy { mutableLeftValueDecoratorProvider.get() }

    @Inject
    lateinit var binaryOperatorTypeRule: IBinaryOperatorTypeRule

    override fun decorate(
        statement: CompoundAssignmentStatement,
        context: StatementDecoratorContext
    ): Diagnosed<Unit> = either {
        val scope = context.scope
        statement.context.relevantScope = scope

        val (targetType, valueType) = zipOrAccumulate(
            { mutableLeftValueDecorator.decorate(statement.leftValue, scope).bindNel() },
            {
                expressionNodeDecorator
                    .decorateExpression(statement.rightValue, ExpressionNodeDecoratorContext(scope))
                    .bindNel()
            }
        ) { target, value -> target to value }

        val resultType = binaryOperatorTypeRule
            .resolve(targetType, valueType, statement.operator)
            .mapLeft { failure ->
                failure.toDiagnostic(
                    statement.parserContext,
                    targetType,
                    valueType,
                    WrittenOperator.Compound(statement.operator)
                ).nel()
            }
            .bind()

        statement.context.type = resultType

        ensure(resultType.canAssignTo(targetType)) {
            TypeMismatch(statement.parserContext, resultType, listOf(targetType)).nel()
        }
    }
}
