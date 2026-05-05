package me.eriknikli.ast.primitives.literals

data class StringLiteral(override val value: String) : Literal<String>
