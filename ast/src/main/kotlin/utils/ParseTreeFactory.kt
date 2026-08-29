package me.eriknikli.rhenium.ast.utils

import arrow.core.left
import arrow.core.right
import arrow.core.toNonEmptyListOrNull
import me.eriknikli.rhenium.ast.diagnostics.SyntaxError
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.parser.RheniumLexer
import me.eriknikli.rhenium.parser.RheniumParser
import me.eriknikli.rhenium.parser.RheniumParser.RootContext
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ParseTreeFactory
@Inject
constructor() : IParseTreeFactory {

    override fun parseStream(stream: CharStream): Diagnosed<RootContext> {
        val errors = ArrayList<SyntaxError>()

        val lexer = RheniumLexer(stream)
        lexer.removeErrorListeners()
        lexer.addErrorListener(errors.collectingListener())

        val tokenStream = CommonTokenStream(lexer)

        val parser = RheniumParser(tokenStream)
        parser.removeErrorListeners()
        parser.addErrorListener(errors.collectingListener())

        val root = parser.root()

        return errors.toNonEmptyListOrNull()?.left() ?: root.right()
    }

    private fun MutableList<SyntaxError>.collectingListener() = object : BaseErrorListener() {
        override fun syntaxError(
            recognizer: Recognizer<*, *>?,
            offendingSymbol: Any?,
            line: Int,
            charPositionInLine: Int,
            msg: String,
            exception: RecognitionException?
        ) {
            add(SyntaxError(line, charPositionInLine + 1, msg))
        }
    }
}
