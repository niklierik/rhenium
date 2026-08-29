package me.eriknikli.rhenium.semanticAnalyzer.exceptions

import me.eriknikli.rhenium.common.location
import org.antlr.v4.runtime.ParserRuleContext

class IllegalStatementException(
    val parserContext: ParserRuleContext,
    val statementType: Class<*>
) : SemanticException(
    "${parserContext.location}: unsupported statement node ${statementType.simpleName}."
)
