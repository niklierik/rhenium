package me.eriknikli.ast.primitives.literals

interface BooleanLiteral : Literal<Boolean>

class TrueLiteral : BooleanLiteral {
    override val value: Boolean = true
}

class FalseLiteral : BooleanLiteral {
    override val value: Boolean = false
}