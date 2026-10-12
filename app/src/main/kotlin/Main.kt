package me.eriknikli.rhenium.app

import me.eriknikli.rhenium.common.diagnostics.hasErrors
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

    val options = CompilerOptions(path)
    val diagnostics = compiler.compile(options)

    if (diagnostics.isNotEmpty()) {
        System.err.println(diagnostics.render())
    }
    if (diagnostics.hasErrors()) {
        exitProcess(1)
    }

    options.binaryPath.runCommand()
}
