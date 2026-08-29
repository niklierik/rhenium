package me.eriknikli.rhenium.common.diagnostics

interface Diagnostic {
    val line: Int
    val column: Int
    val message: String
}
