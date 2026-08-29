package me.eriknikli.rhenium.semanticAnalyzer.exceptions

import me.eriknikli.rhenium.common.location
import org.antlr.v4.runtime.ParserRuleContext

class NotAnLValueException(
    val parserContext: ParserRuleContext,
) : SemanticException(
    "${parserContext.location}: '${parserContext.text}' is not a valid assignment target."
)
