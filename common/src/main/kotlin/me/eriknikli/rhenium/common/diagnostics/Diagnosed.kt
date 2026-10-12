package me.eriknikli.rhenium.common.diagnostics

import arrow.core.EitherNel

typealias Diagnosed<A> = EitherNel<Diagnostic, A>

fun List<Diagnostic>.hasErrors(): Boolean = any { it.severity == Severity.ERROR }

fun List<Diagnostic>.render(): String =
    sortedWith(compareBy({ it.line }, { it.column }))
        .joinToString(System.lineSeparator()) {
            "${it.line}:${it.column}: ${it.severity.name.lowercase()}: ${it.message}"
        }
