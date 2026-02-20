package me.eriknikli.lang.grammar.ast.literals;

public record NullLiteral() implements Literal<Object> {

    @Override
    public Object value() {
        return null;
    }
}
