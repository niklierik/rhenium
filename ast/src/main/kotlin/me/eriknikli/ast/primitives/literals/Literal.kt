package me.eriknikli.ast.primitives.literals

import me.eriknikli.lang.app.ast.Expression

interface Literal<T> : Expression {
    val value: T
}