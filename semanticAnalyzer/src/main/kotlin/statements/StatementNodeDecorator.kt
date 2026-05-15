package me.eriknikli.rhenium.semanticAnalyzer.statements

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.statements.Statement
import me.eriknikli.rhenium.ast.tree.statements.vars.VarDeclarationStatement
import me.eriknikli.rhenium.semanticAnalyzer.expressions.IExpressionNodeDecorator
import javax.inject.Inject
import javax.inject.Singleton

interface IStatementNodeDecorator {
    fun decorateStatement(statement: Statement)
}

@Singleton
class StatementNodeDecorator
@Inject
constructor(

) : IStatementNodeDecorator {
    @Inject
    lateinit var expressionNodeDecoratorProvider: Lazy<IExpressionNodeDecorator>

    private val expressionNodeDecorator by lazy { expressionNodeDecoratorProvider.get() }

    override fun decorateStatement(statement: Statement) {
        when (statement) {
            is VarDeclarationStatement -> {
                val expression = statement.rightSide
                expressionNodeDecorator.decorateExpression(expression)

                statement.context.typeToDeclare = expression.context.type
            }
        }
    }
}