package me.eriknikli.rhenium.semanticAnalyzer.exceptions

import org.antlr.v4.runtime.ParserRuleContext

class NotAnLValueException(
    val parserContext: ParserRuleContext,
) : SemanticException() {
}