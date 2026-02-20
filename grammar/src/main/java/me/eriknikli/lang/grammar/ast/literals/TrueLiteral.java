package me.eriknikli.lang.grammar.ast.literals;

public record TrueLiteral() implements BooleanLiteral {

    @Override
    public Boolean value() {
        return true;
    }
}
