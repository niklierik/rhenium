package me.eriknikli.rhenium.transpiler.utils

import java.io.OutputStream

fun OutputStream.writeText(text: String) {
    val bytes = text.toByteArray(Charsets.UTF_8)
    write(bytes)
}

val lineBreakBytes = System.lineSeparator().toByteArray()

fun OutputStream.writeLineBreak() {
    write(lineBreakBytes)
}