package me.eriknikli.rhenium.semanticContext.scope

import org.antlr.v4.runtime.ParserRuleContext

interface Symbol {
    val cName: String
    val declarationParserContext: ParserRuleContext?
}