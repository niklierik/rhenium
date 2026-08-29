package me.eriknikli.rhenium.semanticAnalyzer.exceptions

import me.eriknikli.rhenium.common.location
import me.eriknikli.rhenium.semanticContext.scope.Symbol
import org.antlr.v4.runtime.ParserRuleContext

class VariableAlreadyDeclaredException(
    val parserContext: ParserRuleContext,
    val name: String,
    val existingSymbol: Symbol? = null
) : SemanticException(
    "${parserContext.location}: variable '$name' is already declared" +
            (existingSymbol?.declarationParserContext?.let { " at ${it.location}" } ?: "") + "."
)
