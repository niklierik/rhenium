package me.eriknikli.rhenium.common

import org.antlr.v4.runtime.ParserRuleContext

val ParserRuleContext.location: String
    get() = "${start.line}:${start.charPositionInLine + 1}"
