package me.eriknikli.rhenium.semanticAnalyzer.exceptions

import org.antlr.v4.runtime.ParserRuleContext

class UnknownTypeException(
    val parserRuleContext: ParserRuleContext,
    val name: String
) : SemanticException() {
}