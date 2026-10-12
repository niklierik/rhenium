package me.eriknikli.rhenium.app

import java.io.File

data class CompilerOptions(val inputPath: String) {
    val cPath: String
        get() = "$inputPath.c"

    val binaryPath: String
        get() = File("$inputPath.o").absolutePath
}
