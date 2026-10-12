package me.eriknikli.rhenium.app

import java.io.File

data class CompilerOptions(val inputPath: String) {
    val binaryPath: String
        get() = File("$inputPath.o").absolutePath
}
