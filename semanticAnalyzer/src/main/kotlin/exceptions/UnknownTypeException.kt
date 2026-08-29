package me.eriknikli.rhenium.semanticAnalyzer.exceptions

import me.eriknikli.rhenium.common.location
import org.antlr.v4.runtime.ParserRuleContext

class UnknownTypeException(
    val parserRuleContext: ParserRuleContext,
    val name: String
) : SemanticException(
    "${parserRuleContext.location}: unknown type '$name'."
)
