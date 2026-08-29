package me.eriknikli.rhenium.semanticAnalyzer.exceptions

import me.eriknikli.rhenium.common.location
import org.antlr.v4.runtime.ParserRuleContext

class IllegalExpressionException(
    val parserContext: ParserRuleContext,
    val expressionType: Class<*>
) : SemanticException(
    "${parserContext.location}: unsupported expression node ${expressionType.simpleName}."
)
