package me.eriknikli.rhenium.semanticAnalyzer.statements

import arrow.core.nel
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.zipOrAccumulate
import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.statements.ReturnStatement
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.MissingReturnValue
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.TypeMismatch
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.UnexpectedReturnValue
import me.eriknikli.rhenium.semanticAnalyzer.expressions.ExpressionNodeDecoratorContext
import me.eriknikli.rhenium.semanticAnalyzer.expressions.IExpressionNodeDecorator
import me.eriknikli.rhenium.semanticContext.scope.types.InvalidType
import me.eriknikli.rhenium.semanticContext.scope.types.VoidType
import javax.inject.Inject
import javax.inject.Singleton

interface IReturnStatementDecorator {
    fun decorate(statement: ReturnStatement, context: StatementDecoratorContext): Diagnosed<Unit>
}

@Singleton
class ReturnStatementDecorator
@Inject
constructor() : IReturnStatementDecorator {
    @Inject
    lateinit var expressionNodeDecoratorProvider: Lazy<IExpressionNodeDecorator>

    private val expressionNodeDecorator by lazy { expressionNodeDecoratorProvider.get() }

    override fun decorate(
        statement: ReturnStatement,
        context: StatementDecoratorContext
    ): Diagnosed<Unit> = either {
        val returnType = context.scope.enclosingReturnType()
            ?: throw IllegalStateException("A return statement is outside every function and entry.")

        val value = statement.value
        if (value == null) {
            ensure(returnType is VoidType || returnType is InvalidType) {
                MissingReturnValue(statement.parserContext, returnType).nel()
            }
            return@either
        }

        val valueContext = ExpressionNodeDecoratorContext(context.scope)
        val valueType = zipOrAccumulate(
            {
                if (returnType is VoidType) {
                    expressionNodeDecorator.decorateDiscardedExpression(value, valueContext).bindNel()
                } else {
                    expressionNodeDecorator.decorateExpression(value, valueContext).bindNel()
                }
            },
            { ensure(returnType !is VoidType) { UnexpectedReturnValue(statement.parserContext) } }
        ) { type, _ -> type }

        ensure(valueType.canAssignTo(returnType)) {
            TypeMismatch(value.parserContext, valueType, listOf(returnType)).nel()
        }
    }
}
