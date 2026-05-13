package me.eriknikli.rhenium.ast

import me.eriknikli.rhenium.ast.utils.IParseTreeFactory
import org.antlr.v4.runtime.CharStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AstBuilder
@Inject
constructor(
    private val parseTreeFactory: IParseTreeFactory
) {
    fun parse(stream: CharStream) {
        val root = parseTreeFactory.parseStream(stream)

    }
}