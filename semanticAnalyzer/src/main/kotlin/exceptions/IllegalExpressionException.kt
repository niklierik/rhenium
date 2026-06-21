package me.eriknikli.rhenium.semanticAnalyzer.exceptions

import org.antlr.v4.runtime.ParserRuleContext

class IllegalExpressionException(
    val parserContext: ParserRuleContext,
    val expressionType: Class<*>
) : SemanticException() {
}