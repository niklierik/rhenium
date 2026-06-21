package me.eriknikli.rhenium.semanticAnalyzer.statements

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.statements.vars.VarDeclarationStatement
import me.eriknikli.rhenium.common.and
import me.eriknikli.rhenium.semanticAnalyzer.exceptions.TypeMismatchException
import me.eriknikli.rhenium.semanticAnalyzer.exceptions.UnknownTypeException
import me.eriknikli.rhenium.semanticAnalyzer.exceptions.VariableAlreadyDeclaredException
import me.eriknikli.rhenium.semanticAnalyzer.expressions.ExpressionNodeDecoratorContext
import me.eriknikli.rhenium.semanticAnalyzer.expressions.IExpressionNodeDecorator
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import me.eriknikli.rhenium.semanticContext.scope.vars.Variable
import javax.inject.Inject
import javax.inject.Singleton

interface IVarDeclarationStatementDecorator {
    fun decorate(statement: VarDeclarationStatement)
}


@Singleton
class VarDeclarationStatementDecorator
@Inject constructor() : IVarDeclarationStatementDecorator {
    @Inject
    lateinit var expressionNodeDecoratorProvider: Lazy<IExpressionNodeDecorator>

    private val expressionNodeDecorator by lazy { expressionNodeDecoratorProvider.get() }

    override fun decorate(statement: VarDeclarationStatement) {
        val scope = statement.context.relevantScope
        val expression = statement.rightSide
        val name = statement.name

        and(
            {
                expressionNodeDecorator.decorateExpression(expression, ExpressionNodeDecoratorContext(scope))
            },
            {
                val existingVariable = scope.getDirectSymbol(name)
                if (existingVariable != null) {
                    throw VariableAlreadyDeclaredException(
                        statement.parserContext,
                        name,
                        existingVariable
                    )
                }
            }
        )
        val actualType = expression.context.type
        var expectedType = actualType
        statement.expectedType?.let {
            val expectedTypeName = it.toString()
            val maybeType = scope.getSymbol(expectedTypeName)

            if (maybeType !is ExpressionType) {
                throw UnknownTypeException(it.parserContext, expectedTypeName)
            }

            expectedType = maybeType

            val isValidTypeDeclaration = actualType.canAssignTo(expectedType)

            if (!isValidTypeDeclaration) {
                throw TypeMismatchException(statement.parserContext, actualType, expectedType)
            }
        }

        statement.context.typeToDeclare = expectedType

        val mutable = statement.mutable
        val variable = Variable(name, expectedType, mutable, statement.parserContext)
        scope.insertSymbol(name, variable)
        statement.context.symbolInfo = variable

    }
}