package me.eriknikli.rhenium.ast.tree.expressions.operators

enum class Operator(val cString: String) {
    PLUS("+"),
    MINUS("-"),
    STAR("*"),
    SLASH("/"),
    PERCENT("%"),
    HAT("^"),
    BANG("!"),
    LESS("<"),
    GREATER(">"),
    LESS_EQUALS("<="),
    GREATER_EQUALS(">="),
    EQUALS("=="),
    NOT_EQUALS("!="),
    AND("&&"),
    OR("||")
}