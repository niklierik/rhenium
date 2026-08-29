package me.eriknikli.rhenium.semanticAnalyzer.exceptions

import me.eriknikli.rhenium.ast.tree.expressions.Identifier
import me.eriknikli.rhenium.common.location
import org.antlr.v4.runtime.ParserRuleContext

class UnknownSymbolException(
    val parserContext: ParserRuleContext,
    val identifier: Identifier
) : SemanticException(
    "${parserContext.location}: unknown symbol '$identifier'."
)
