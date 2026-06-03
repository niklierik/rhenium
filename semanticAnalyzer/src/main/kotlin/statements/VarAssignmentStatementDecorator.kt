package me.eriknikli.rhenium.semanticAnalyzer.statements

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.statements.vars.VarAssignmentStatement
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

        val right = statement.rightValue
        expressionNodeDecorator.decorateExpression(right, ExpressionNodeDecoratorContext(scope))

        val left = statement.leftValue
        expressionNodeDecorator.decorateExpression(left, ExpressionNodeDecoratorContext(scope))
        if (left.context !is LValueContext) {
            throw Exception("Left side in variable assignment is not a valid left-value.")
        }

        val validAssignment = right.context.type.canAssignTo(left.context.type)
        if (!validAssignment) {
            throw Exception("Expression with type of ${right.context.type} cannot be assigned to type of ${left.context.type}.")
        }

        statement.context.relevantScope = scope
    }
}