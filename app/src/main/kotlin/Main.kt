package me.eriknikli.rhenium.app

import me.eriknikli.rhenium.common.diagnostics.render
import me.eriknikli.rhenium.common.runCommand
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val path = args.getOrElse(0) { "main.re" }
    val entryName = args.getOrNull(1)

    if (!File(path).exists()) {
        System.err.println("Input file '$path' not found.")
        exitProcess(1)
    }

    val factory = DaggerRheniumCompilerFactory.create()
    val compiler = factory.makeCompiler()

    val compilation = compiler.compile(CompilerOptions(path, entryName))

    if (compilation.diagnostics.isNotEmpty()) {
        System.err.println(compilation.diagnostics.render())
    }

    compilation.problem?.let { System.err.println(it) }

    val binaryPath = compilation.binaryPath ?: exitProcess(1)
    binaryPath.runCommand()
}
