package me.eriknikli.rhenium.semanticAnalyzer.statements

import arrow.core.getOrElse
import arrow.core.leftNel
import arrow.core.nel
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.zipOrAccumulate
import arrow.core.right
import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.expressions.Identifier
import me.eriknikli.rhenium.ast.tree.statements.vars.VarDeclarationStatement
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.TypeMismatch
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.UnknownType
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.VariableAlreadyDeclared
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.VariableShadowsOuter
import me.eriknikli.rhenium.semanticAnalyzer.expressions.ExpressionNodeDecoratorContext
import me.eriknikli.rhenium.semanticAnalyzer.expressions.IExpressionNodeDecorator
import me.eriknikli.rhenium.semanticContext.scope.LeftValueSymbol
import me.eriknikli.rhenium.semanticContext.scope.Scope
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import me.eriknikli.rhenium.semanticContext.scope.types.InvalidType
import me.eriknikli.rhenium.semanticContext.scope.vars.Variable
import javax.inject.Inject
import javax.inject.Singleton

interface IVarDeclarationStatementDecorator {
    fun decorate(statement: VarDeclarationStatement): Diagnosed<Unit>
}

@Singleton
class VarDeclarationStatementDecorator
@Inject constructor() : IVarDeclarationStatementDecorator {
    @Inject
    lateinit var expressionNodeDecoratorProvider: Lazy<IExpressionNodeDecorator>

    private val expressionNodeDecorator by lazy { expressionNodeDecoratorProvider.get() }

    override fun decorate(statement: VarDeclarationStatement): Diagnosed<Unit> {
        val scope = statement.context.relevantScope
        val name = statement.name

        val declaredType = either {
            val (actualType, expectedType) = zipOrAccumulate(
                {
                    expressionNodeDecorator
                        .decorateExpression(statement.rightSide, ExpressionNodeDecoratorContext(scope))
                        .bindNel()
                },
                {
                    statement.expectedType?.let { declaredTypeOf(it, scope).bindNel() }
                },
                {
                    nameIsFree(statement, scope, name).bindNel()
                }
            ) { actual, expected, _ -> actual to expected }

            val declaredType = expectedType ?: actualType
            ensure(actualType.canAssignTo(declaredType)) {
                TypeMismatch(statement.parserContext, actualType, listOf(declaredType)).nel()
            }

            declaredType
        }

        declare(statement, scope, declaredType.getOrElse { InvalidType })

        return declaredType.map { }
    }

    private fun nameIsFree(
        statement: VarDeclarationStatement,
        scope: Scope,
        name: String
    ): Diagnosed<Unit> {
        val declaredHere = scope.getDirectSymbol(name)
        if (declaredHere != null) {
            return VariableAlreadyDeclared(statement.parserContext, name, declaredHere).leftNel()
        }

        val outer = scope.getSymbol(name) ?: return Unit.right()

        return if (outer is LeftValueSymbol) {
            VariableShadowsOuter(statement.parserContext, name, outer).leftNel()
        } else {
            VariableAlreadyDeclared(statement.parserContext, name, outer).leftNel()
        }
    }

    private fun declaredTypeOf(expectedType: Identifier, scope: Scope): Diagnosed<ExpressionType> {
        val name = expectedType.toString()
        val symbol = scope.getSymbol(name)

        if (symbol !is ExpressionType) {
            return UnknownType(expectedType.parserContext, name).leftNel()
        }

        return symbol.right()
    }

    private fun declare(statement: VarDeclarationStatement, scope: Scope, type: ExpressionType) {
        val variable = Variable(statement.name, type, statement.mutable, statement.parserContext)

        statement.context.typeToDeclare = type
        statement.context.symbolInfo = variable

        val taken = scope.getSymbol(statement.name)
        val takenByAType = taken != null && taken !is LeftValueSymbol

        if (scope.getDirectSymbol(statement.name) == null && !takenByAType) {
            scope.insertSymbol(statement.name, variable)
        }
    }
}
