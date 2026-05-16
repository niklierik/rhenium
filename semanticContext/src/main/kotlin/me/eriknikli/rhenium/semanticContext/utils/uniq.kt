package me.eriknikli.rhenium.semanticContext.utils

import java.util.*

fun uniq(): String {
    return UUID.randomUUID().toString().replace('-', '_')
}