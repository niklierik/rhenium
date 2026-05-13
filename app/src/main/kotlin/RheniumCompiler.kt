package me.eriknikli.rhenium.app

import me.eriknikli.rhenium.ast.IAstBuilder
import org.antlr.v4.runtime.CharStreams
import javax.inject.Inject
import javax.inject.Singleton

interface IRheniumCompiler {
    fun compile(options: CompilerOptions)
}

@Singleton
class RheniumCompiler
@Inject
constructor(
    private val astBuilder: IAstBuilder
) : IRheniumCompiler {
    override fun compile(options: CompilerOptions) {
        val stream = CharStreams.fromFileName(options.inputPath)
        val ast = astBuilder.parse(stream)
        println(ast)
    }
}