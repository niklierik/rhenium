package me.eriknikli.rhenium.ast.tree.expressions.literals

data class I64Literal(
    override val value: Long
) : Literal<Long>
