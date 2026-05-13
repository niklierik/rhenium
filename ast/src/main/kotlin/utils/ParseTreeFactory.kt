package me.eriknikli.rhenium.ast.utils

import me.eriknikli.rhenium.parser.RheniumLexer
import me.eriknikli.rhenium.parser.RheniumParser
import me.eriknikli.rhenium.parser.RheniumParser.RootContext
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CommonTokenStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ParseTreeFactory
@Inject
constructor() : IParseTreeFactory {

    override fun parseStream(stream: CharStream): RootContext {
        val lexer = RheniumLexer(stream)
        val tokenStream = CommonTokenStream(lexer)
        val parser = RheniumParser(tokenStream)

        return parser.root()
    }

}