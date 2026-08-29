package me.eriknikli.rhenium.common

import org.antlr.v4.runtime.ParserRuleContext

val ParserRuleContext.line: Int
    get() = start.line

val ParserRuleContext.column: Int
    get() = start.charPositionInLine + 1

val ParserRuleContext.location: String
    get() = "$line:$column"
