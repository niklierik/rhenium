package me.eriknikli.lang.grammar.ast.literals;

public record FalseLiteral() implements BooleanLiteral {

    @Override
    public Boolean value() {
        return false;
    }
}
