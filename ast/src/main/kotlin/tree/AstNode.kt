package me.eriknikli.rhenium.ast.tree

import org.antlr.v4.runtime.ParserRuleContext

interface AstNode {
    val parserContext: ParserRuleContext
}