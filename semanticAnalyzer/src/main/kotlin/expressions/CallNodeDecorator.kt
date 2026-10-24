package me.eriknikli.rhenium.semanticAnalyzer.expressions

import arrow.core.nel
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.mapOrAccumulate
import arrow.core.raise.zipOrAccumulate
import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.expressions.CallExpression
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.ArgumentCountMismatch
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.NotAFunction
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.TypeMismatch
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.UnknownSymbol
import me.eriknikli.rhenium.semanticContext.scope.functions.FunctionSymbol
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import me.eriknikli.rhenium.semanticContext.scope.types.InvalidType
import javax.inject.Inject
import javax.inject.Singleton

interface ICallNodeDecorator {
    fun decorate(call: CallExpression, context: ExpressionNodeDecoratorContext): Diagnosed<ExpressionType>
}

@Singleton
class CallNodeDecorator
@Inject
constructor() : ICallNodeDecorator {
    @Inject
    lateinit var expressionNodeDecoratorProvider: Lazy<IExpressionNodeDecorator>

    private val expressionNodeDecorator by lazy { expressionNodeDecoratorProvider.get() }

    override fun decorate(call: CallExpression, context: ExpressionNodeDecoratorContext): Diagnosed<ExpressionType> {
        call.context.relevantScope = context.scope
        call.context.type = InvalidType

        return either {
            val (function, argumentTypes) = zipOrAccumulate(
                { calleeOf(call, context).bindNel() },
                { mapOrAccumulate(call.arguments) { expressionNodeDecorator.decorateExpression(it, context).bindNel() } }
            ) { function, argumentTypes -> function to argumentTypes }

            val parameterTypes = function.signature.parameterTypes
            ensure(argumentTypes.size == parameterTypes.size) {
                ArgumentCountMismatch(call.parserContext, function.name, parameterTypes.size, argumentTypes.size).nel()
            }

            mapOrAccumulate(call.arguments.zip(argumentTypes.zip(parameterTypes))) { (argument, types) ->
                val (argumentType, parameterType) = types
                ensure(argumentType.canAssignTo(parameterType)) {
                    TypeMismatch(argument.parserContext, argumentType, listOf(parameterType))
                }
            }

            call.context.function = function
            call.context.type = function.signature.returnType
            function.signature.returnType
        }
    }

    private fun calleeOf(call: CallExpression, context: ExpressionNodeDecoratorContext): Diagnosed<FunctionSymbol> =
        either {
            val callee = call.callee
            val symbol = context.scope.getSymbol(callee.id)
                ?: raise(UnknownSymbol(callee.parserContext, callee).nel())

            ensure(symbol is FunctionSymbol) { NotAFunction(callee.parserContext, callee).nel() }
            symbol
        }
}
