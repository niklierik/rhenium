package me.eriknikli.rhenium.common

import java.io.File
import java.util.concurrent.TimeUnit

fun String.runCommand(workingDir: File = File(".")): Int {
    val process = ProcessBuilder(*split(" ").toTypedArray())
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .start()
    if (!process.waitFor(60, TimeUnit.MINUTES)) {
        process.destroyForcibly()
    }
    return process.waitFor()
}