package me.eriknikli.rhenium.semanticAnalyzer.exceptions

import org.antlr.v4.runtime.ParserRuleContext

class IllegalStatementException(
    val parserContext: ParserRuleContext,
    val statementType: Class<*>
) : SemanticException() {
}