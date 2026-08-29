package me.eriknikli.rhenium.semanticAnalyzer.expressions

import arrow.core.leftNel
import arrow.core.right
import me.eriknikli.rhenium.ast.tree.expressions.Identifier
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.InvalidLeftValueSymbol
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.UnknownSymbol
import me.eriknikli.rhenium.semanticContext.scope.LeftValueSymbol
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import me.eriknikli.rhenium.semanticContext.scope.vars.ErrorSymbol
import javax.inject.Inject
import javax.inject.Singleton

interface IIdentifierNodeDecorator {
    fun decorateIdentifier(
        identifier: Identifier,
        context: ExpressionNodeDecoratorContext
    ): Diagnosed<ExpressionType>
}

@Singleton
class IdentifierNodeDecorator
@Inject
constructor() : IIdentifierNodeDecorator {
    override fun decorateIdentifier(
        identifier: Identifier,
        context: ExpressionNodeDecoratorContext
    ): Diagnosed<ExpressionType> {
        val scope = context.scope
        identifier.context.relevantScope = scope

        val symbol = scope.getSymbol(identifier.id)

        if (symbol == null) {
            identifier.context.symbol = ErrorSymbol
            return UnknownSymbol(identifier.parserContext, identifier).leftNel()
        }

        if (symbol !is LeftValueSymbol) {
            identifier.context.symbol = ErrorSymbol
            return InvalidLeftValueSymbol(identifier.parserContext, symbol).leftNel()
        }

        identifier.context.symbol = symbol

        return symbol.declaredType.right()
    }
}
