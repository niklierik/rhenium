package me.eriknikli.rhenium.semanticAnalyzer.statements

import arrow.core.nel
import arrow.core.raise.either
import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.statements.vars.IncrementStatement
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.IllegalIncrement
import me.eriknikli.rhenium.semanticAnalyzer.expressions.BinaryOperatorFailure
import me.eriknikli.rhenium.semanticAnalyzer.expressions.IBinaryOperatorTypeRule
import javax.inject.Inject
import javax.inject.Singleton

interface IIncrementStatementDecorator {
    fun decorate(statement: IncrementStatement, context: StatementDecoratorContext): Diagnosed<Unit>
}

@Singleton
class IncrementStatementDecorator
@Inject
constructor() : IIncrementStatementDecorator {
    @Inject
    lateinit var mutableLeftValueDecoratorProvider: Lazy<IMutableLeftValueDecorator>

    private val mutableLeftValueDecorator by lazy { mutableLeftValueDecoratorProvider.get() }

    @Inject
    lateinit var binaryOperatorTypeRule: IBinaryOperatorTypeRule

    override fun decorate(
        statement: IncrementStatement,
        context: StatementDecoratorContext
    ): Diagnosed<Unit> = either {
        statement.context.relevantScope = context.scope

        val type = mutableLeftValueDecorator.decorate(statement.leftValue, context.scope).bind()

        statement.context.type = binaryOperatorTypeRule
            .resolve(type, type, statement.operator)
            .mapLeft { failure ->
                when (failure) {
                    BinaryOperatorFailure.ILLEGAL_OPERATION ->
                        IllegalIncrement(statement.parserContext, type, statement.operator).nel()

                    BinaryOperatorFailure.MIXED_SIGNEDNESS -> throw IllegalStateException(
                        "A type cannot mix signedness with itself, yet $type did."
                    )
                }
            }
            .bind()
    }
}
