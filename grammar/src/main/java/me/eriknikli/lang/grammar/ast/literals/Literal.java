package me.eriknikli.lang.grammar.ast.literals;

import me.eriknikli.lang.grammar.ast.Expression;

public interface Literal<T> extends Expression {
    T value();
}
