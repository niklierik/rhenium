package me.eriknikli.rhenium.app

import me.eriknikli.rhenium.common.diagnostics.Diagnostic

data class Compilation(val diagnostics: List<Diagnostic>, val binaryPath: String?)
