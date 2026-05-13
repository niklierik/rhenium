package me.eriknikli.rhenium.ast.utils

import me.eriknikli.rhenium.parser.RheniumParser.RootContext
import org.antlr.v4.runtime.CharStream

interface IParseTreeFactory {
    fun parseStream(stream: CharStream): RootContext
}