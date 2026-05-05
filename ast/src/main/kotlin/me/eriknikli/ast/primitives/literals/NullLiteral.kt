package me.eriknikli.ast.primitives.literals

class NullLiteral : Literal<Unit> {
    override val value: Unit = Unit
}