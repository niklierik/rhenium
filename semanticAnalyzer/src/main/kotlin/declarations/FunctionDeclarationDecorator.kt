package me.eriknikli.rhenium.semanticAnalyzer.declarations

import arrow.core.getOrElse
import arrow.core.leftNel
import arrow.core.nel
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.mapOrAccumulate
import arrow.core.raise.zipOrAccumulate
import arrow.core.right
import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.FunctionDeclaration
import me.eriknikli.rhenium.ast.tree.Parameter
import me.eriknikli.rhenium.ast.tree.statements.BlockStatement
import me.eriknikli.rhenium.ast.tree.statements.IfStatement
import me.eriknikli.rhenium.ast.tree.statements.ReturnStatement
import me.eriknikli.rhenium.ast.tree.statements.Statement
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.FunctionAlreadyDeclared
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.MissingReturn
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.VariableAlreadyDeclared
import me.eriknikli.rhenium.semanticAnalyzer.statements.IStatementNodeDecorator
import me.eriknikli.rhenium.semanticAnalyzer.statements.StatementDecoratorContext
import me.eriknikli.rhenium.semanticAnalyzer.statements.allowsBareReturn
import me.eriknikli.rhenium.semanticContext.scope.Scope
import me.eriknikli.rhenium.semanticContext.scope.functions.FunctionSymbol
import me.eriknikli.rhenium.semanticContext.scope.functions.Signature
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import me.eriknikli.rhenium.semanticContext.scope.types.InvalidType
import me.eriknikli.rhenium.semanticContext.scope.types.VoidType
import me.eriknikli.rhenium.semanticContext.scope.vars.Variable
import javax.inject.Inject
import javax.inject.Singleton

interface IFunctionDeclarationDecorator {
    fun declare(function: FunctionDeclaration, globalScope: Scope): Diagnosed<Unit>

    fun decorateBody(function: FunctionDeclaration): Diagnosed<Unit>
}

@Singleton
class FunctionDeclarationDecorator
@Inject
constructor() : IFunctionDeclarationDecorator {
    @Inject
    lateinit var typeNameDecoratorProvider: Lazy<ITypeNameDecorator>

    @Inject
    lateinit var statementNodeDecoratorProvider: Lazy<IStatementNodeDecorator>

    private val typeNameDecorator by lazy { typeNameDecoratorProvider.get() }
    private val statementNodeDecorator by lazy { statementNodeDecoratorProvider.get() }

    override fun declare(function: FunctionDeclaration, globalScope: Scope): Diagnosed<Unit> {
        val parameterTypes = function.parameters.map { typeNameDecorator.valueTypeOf(it.type, globalScope) }
        val returnType = function.returnType?.let { typeNameDecorator.typeOf(it, globalScope) } ?: VoidType.right()

        val signature = Signature(
            parameterTypes.map { it.getOrElse { InvalidType } },
            returnType.getOrElse { InvalidType }
        )
        val symbol = FunctionSymbol(function.name, signature, function.parserContext)
        function.context.relevantScope = globalScope
        function.context.symbol = symbol

        val existing = globalScope.getDirectSymbol(function.name)
        if (existing == null) {
            globalScope.insertSymbol(function.name, symbol)
        }

        return either {
            zipOrAccumulate(
                { mapOrAccumulate(parameterTypes) { it.bindNel() } },
                { returnType.bindNel() },
                { ensure(existing == null) { FunctionAlreadyDeclared(function.parserContext, function.name, existing.declarationParserContext) } }
            ) { _, _, _ -> }
        }
    }

    override fun decorateBody(function: FunctionDeclaration): Diagnosed<Unit> = either {
        val signature = function.context.symbol.signature
        val functionScope = function.context.relevantScope.createFunctionChild(signature.returnType)

        zipOrAccumulate(
            {
                mapOrAccumulate(function.parameters.zip(signature.parameterTypes)) { (parameter, type) ->
                    declareParameter(parameter, type, functionScope).bindNel()
                }
            },
            {
                statementNodeDecorator
                    .decorateStatement(function.body, StatementDecoratorContext(functionScope))
                    .bindNel()
            },
            { checkReturns(function, signature.returnType).bindNel() }
        ) { _, _, _ -> }
    }

    private fun checkReturns(function: FunctionDeclaration, returnType: ExpressionType): Diagnosed<Unit> = either {
        if (returnType.allowsBareReturn()) {
            return@either
        }

        ensure(alwaysReturns(function.body)) { MissingReturn(function.parserContext, function.name).nel() }
    }

    private fun alwaysReturns(statement: Statement): Boolean = when (statement) {
        is ReturnStatement -> true
        is BlockStatement -> statement.statements.any { alwaysReturns(it) }
        is IfStatement -> statement.elseBranch?.let { alwaysReturns(statement.thenBranch) && alwaysReturns(it) } ?: false
        else -> false
    }

    private fun declareParameter(parameter: Parameter, type: ExpressionType, scope: Scope): Diagnosed<Unit> {
        val existing = scope.getSymbol(parameter.name)
        val variable = Variable(
            parameter.name,
            if (existing == null) type else InvalidType,
            parameter.mutable,
            parameter.parserContext
        )
        parameter.context.relevantScope = scope
        parameter.context.variable = variable

        if (scope.getDirectSymbol(parameter.name) == null) {
            scope.insertSymbol(parameter.name, variable)
        }

        if (existing != null) {
            return VariableAlreadyDeclared(parameter.parserContext, parameter.name, existing).leftNel()
        }

        return Unit.right()
    }
}
