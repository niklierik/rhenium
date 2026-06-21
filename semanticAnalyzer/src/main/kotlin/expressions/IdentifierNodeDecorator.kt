package me.eriknikli.rhenium.semanticAnalyzer.expressions

import me.eriknikli.rhenium.ast.tree.expressions.Identifier
import me.eriknikli.rhenium.semanticAnalyzer.exceptions.InvalidLeftValueSymbolException
import me.eriknikli.rhenium.semanticAnalyzer.exceptions.UnknownSymbolException
import me.eriknikli.rhenium.semanticContext.scope.LeftValueSymbol
import javax.inject.Inject
import javax.inject.Singleton

interface IIdentifierNodeDecorator {
    fun decorateIdentifier(identifier: Identifier, context: ExpressionNodeDecoratorContext)
}

@Singleton
class IdentifierNodeDecorator
@Inject
constructor() : IIdentifierNodeDecorator {
    override fun decorateIdentifier(identifier: Identifier, context: ExpressionNodeDecoratorContext) {
        val scope = context.scope
        val symbol = scope.getSymbol(identifier.id)
        if (symbol == null) {
            throw UnknownSymbolException(identifier.parserContext, identifier)
        }
        if (symbol !is LeftValueSymbol) {
            throw InvalidLeftValueSymbolException(identifier.parserContext, symbol)
        }

        identifier.context.relevantScope = scope
        identifier.context.symbol = symbol
    }
}