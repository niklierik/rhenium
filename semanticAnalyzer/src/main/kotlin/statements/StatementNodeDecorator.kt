package me.eriknikli.rhenium.semanticAnalyzer.statements

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.statements.Statement
import me.eriknikli.rhenium.ast.tree.statements.vars.VarDeclarationStatement
import me.eriknikli.rhenium.semanticAnalyzer.expressions.ExpressionNodeDecoratorContext
import me.eriknikli.rhenium.semanticAnalyzer.expressions.IExpressionNodeDecorator
import me.eriknikli.rhenium.semanticContext.scope.Scope
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import me.eriknikli.rhenium.semanticContext.scope.vars.Variable
import javax.inject.Inject
import javax.inject.Singleton

data class StatementDecoratorContext(
    val scope: Scope
)

interface IStatementNodeDecorator {
    fun decorateStatement(statement: Statement, statementDecoratorContext: StatementDecoratorContext)
}

@Singleton
class StatementNodeDecorator
@Inject
constructor(

) : IStatementNodeDecorator {
    @Inject
    lateinit var expressionNodeDecoratorProvider: Lazy<IExpressionNodeDecorator>

    private val expressionNodeDecorator by lazy { expressionNodeDecoratorProvider.get() }

    override fun decorateStatement(
        statement: Statement,
        statementDecoratorContext: StatementDecoratorContext
    ) {
        val scope = statementDecoratorContext.scope
        statement.context.relevantScope = scope

        when (statement) {
            is VarDeclarationStatement -> {
                val expression = statement.rightSide
                expressionNodeDecorator.decorateExpression(expression, ExpressionNodeDecoratorContext(scope))

                val name = statement.name
                val existingVariable = scope.getDirectSymbol(name)
                if (existingVariable != null) {
                    throw Exception("Variable '${name}' is already declared.")
                }

                val actualType = expression.context.type
                var expectedType = actualType


                val expectedTypeName = statement.expectedType?.toString()
                if (expectedTypeName != null) {
                    val maybeType = scope.getSymbol(expectedTypeName)

                    if (maybeType !is ExpressionType) {
                        throw Exception("Symbol '${expectedTypeName}' is not a valid type.")
                    }

                    expectedType = maybeType

                    val isValidTypeDeclaration = actualType.canAssignTo(expectedType)

                    if (!isValidTypeDeclaration) {
                        throw Exception("Variable declared as '${expectedType}' cannot have a value of type '${actualType}'.")
                    }
                }

                statement.context.typeToDeclare = expectedType

                val mutable = statement.mutable
                val variable = Variable(name, expectedType, mutable)
                scope.insertSymbol(name, variable)
            }
        }
    }
}