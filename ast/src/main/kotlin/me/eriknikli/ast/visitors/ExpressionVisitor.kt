package me.eriknikli.ast.visitors

import me.eriknikli.ast.Expression
import me.eriknikli.lang.grammar.LangBaseVisitor
import me.eriknikli.lang.grammar.LangParser

class ExpressionVisitor : LangBaseVisitor<Expression>() {
    override fun visitExpression(ctx: LangParser.ExpressionContext?): Expression {
        return super.visitExpression(ctx)
    }
}