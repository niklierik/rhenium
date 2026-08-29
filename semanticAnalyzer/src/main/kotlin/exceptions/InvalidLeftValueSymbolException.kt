package me.eriknikli.rhenium.semanticAnalyzer.exceptions

import me.eriknikli.rhenium.common.location
import me.eriknikli.rhenium.semanticContext.scope.Symbol
import org.antlr.v4.runtime.ParserRuleContext

class InvalidLeftValueSymbolException(
    val parserContext: ParserRuleContext,
    val symbol: Symbol
) : SemanticException(
    "${parserContext.location}: '${parserContext.text}' does not name a value."
)
