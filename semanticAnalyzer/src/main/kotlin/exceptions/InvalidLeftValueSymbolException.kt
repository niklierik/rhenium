package me.eriknikli.rhenium.semanticAnalyzer.exceptions

import me.eriknikli.rhenium.semanticContext.scope.Symbol
import org.antlr.v4.runtime.ParserRuleContext

class InvalidLeftValueSymbolException(
    parserContext: ParserRuleContext,
    symbol: Symbol
) : SemanticException() {
}