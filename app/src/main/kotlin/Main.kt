package me.eriknikli.rhenium.app

import java.io.File
import java.io.FileNotFoundException

fun main(args: Array<String>) {
    var path = args.joinToString(" ")
    if (path.isEmpty()) {
        path = "main.re"
    }

    if (!File(path).exists()) {
        throw FileNotFoundException("Input file '$path' not found.")
    }

    val factory = DaggerRheniumCompilerFactory.create()
    val compiler = factory.makeCompiler()

}