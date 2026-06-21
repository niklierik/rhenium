package me.eriknikli.rhenium.semanticAnalyzer.exceptions

import me.eriknikli.rhenium.ast.tree.expressions.Identifier
import org.antlr.v4.runtime.ParserRuleContext

class UnknownSymbolException(
    parserContext: ParserRuleContext,
    identifier: Identifier
) : SemanticException() {
}