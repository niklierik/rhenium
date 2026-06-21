package me.eriknikli.rhenium.semanticAnalyzer.exceptions

import me.eriknikli.rhenium.ast.tree.expressions.LeftValue
import org.antlr.v4.runtime.ParserRuleContext

class ImmutableLeftValueException(
    parserContext: ParserRuleContext,
    leftValue: LeftValue
) : SemanticException() {
}