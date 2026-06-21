package me.eriknikli.rhenium.semanticAnalyzer.statements

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.statements.vars.VarAssignmentStatement
import me.eriknikli.rhenium.common.and
import me.eriknikli.rhenium.semanticAnalyzer.exceptions.NotAnLValueException
import me.eriknikli.rhenium.semanticAnalyzer.exceptions.TypeMismatchException
import me.eriknikli.rhenium.semanticAnalyzer.expressions.ExpressionNodeDecoratorContext
import me.eriknikli.rhenium.semanticAnalyzer.expressions.IExpressionNodeDecorator
import me.eriknikli.rhenium.semanticContext.tree.expressions.LValueContext
import javax.inject.Inject
import javax.inject.Singleton

interface IVarAssignmentStatementDecorator {
    fun decorate(statement: VarAssignmentStatement, context: StatementDecoratorContext)
}

@Singleton
class VarAssignmentStatementDecorator
@Inject
constructor() : IVarAssignmentStatementDecorator {
    @Inject
    lateinit var expressionNodeDecoratorProvider: Lazy<IExpressionNodeDecorator>

    private val expressionNodeDecorator by lazy { expressionNodeDecoratorProvider.get() }

    override fun decorate(statement: VarAssignmentStatement, context: StatementDecoratorContext) {
        val scope = context.scope

        val (left, right) = and(
            {
                statement.leftValue.let {
                    expressionNodeDecorator.decorateExpression(it, ExpressionNodeDecoratorContext(scope))
                    if (it.context !is LValueContext) {
                        throw NotAnLValueException(it.parserContext)
                    }
                    it
                }
            },
            {
                statement.rightValue.let {
                    expressionNodeDecorator.decorateExpression(it, ExpressionNodeDecoratorContext(scope))
                    it
                }
            }
        )

        val actualType = right.context.type
        val expectedType = left.context.type
        val validAssignment = actualType.canAssignTo(expectedType)
        if (!validAssignment) {
            throw TypeMismatchException(
                statement.parserContext,
                actualType,
                expectedType
            )
        }

        statement.context.relevantScope = scope
    }
}