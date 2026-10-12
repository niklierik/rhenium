package me.eriknikli.rhenium.app

import me.eriknikli.rhenium.common.diagnostics.render
import me.eriknikli.rhenium.common.runCommand
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

    val compilation = compiler.compile(CompilerOptions(path))

    if (compilation.diagnostics.isNotEmpty()) {
        System.err.println(compilation.diagnostics.render())
    }

    val binaryPath = compilation.binaryPath ?: exitProcess(1)
    binaryPath.runCommand()
}
