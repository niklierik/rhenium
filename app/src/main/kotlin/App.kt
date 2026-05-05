package me.eriknikli.lang.app

import com.google.inject.Guice
import me.eriknikli.lang.app.visitors.StartVisitor
import me.eriknikli.lang.grammar.LangLexer
import me.eriknikli.lang.grammar.LangParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream


fun main() {
    val module = LangDIModule()
    val injector = Guice.createInjector(listOf(module))
    module.injector = injector

    val startVisitor = injector.getInstance(StartVisitor::class.java)

    val stream = CharStreams.fromFileName("experiment/input.txt")
    val lexer = LangLexer(stream)
    val tokenStream = CommonTokenStream(lexer)
    val parser = LangParser(tokenStream)
    val start = parser.start()

    val expression = startVisitor.visitStart(start)
    println(expression.toString())
}
