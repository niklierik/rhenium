package me.eriknikli.rhenium.app

import me.eriknikli.rhenium.common.diagnostics.render
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    var path = args.joinToString(" ")
    if (path.isEmpty()) {
        path = "main.re"
    }

    if (!File(path).exists()) {
        System.err.println("Input file '$path' not found.")
        exitProcess(1)
    }

    val factory = DaggerRheniumCompilerFactory.create()
    val compiler = factory.makeCompiler()

    compiler.compile(CompilerOptions(path)).onLeft { diagnostics ->
        System.err.println(diagnostics.render())
        exitProcess(1)
    }
}
