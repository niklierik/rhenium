package me.eriknikli.rhenium.semanticAnalyzer.exceptions

import me.eriknikli.rhenium.ast.tree.expressions.LeftValue
import me.eriknikli.rhenium.common.location
import org.antlr.v4.runtime.ParserRuleContext

class ImmutableLeftValueException(
    val parserContext: ParserRuleContext,
    val leftValue: LeftValue
) : SemanticException(
    "${parserContext.location}: cannot assign to '$leftValue', it is not mutable. Declare it with 'let'."
)
