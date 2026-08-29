package me.eriknikli.rhenium.common.diagnostics

import arrow.core.EitherNel
import arrow.core.NonEmptyList

typealias Diagnosed<A> = EitherNel<Diagnostic, A>

fun NonEmptyList<Diagnostic>.render(): String =
    sortedWith(compareBy({ it.line }, { it.column }))
        .joinToString(System.lineSeparator()) { "${it.line}:${it.column}: ${it.message}" }
