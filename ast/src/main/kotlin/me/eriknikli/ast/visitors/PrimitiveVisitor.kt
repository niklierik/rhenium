package me.eriknikli.ast.visitors

import com.google.inject.Inject
import com.google.inject.Singleton
import me.eriknikli.lang.app.ast.Expression
import me.eriknikli.lang.app.ast.Identifier
import me.eriknikli.lang.app.ast.primitives.GroupExpression
import me.eriknikli.lang.common.di.DI
import me.eriknikli.lang.grammar.LangBaseVisitor
import me.eriknikli.lang.grammar.LangParser

@Singleton
class PrimitiveVisitor
@Inject
constructor(
    private val di: DI,
    private val literalVisitor: LiteralVisitor,
) : LangBaseVisitor<Expression>() {

    private val expressionVisitor by lazy { di.inject<ExpressionVisitor>() }

    override fun visitIdPrimitive(ctx: LangParser.IdPrimitiveContext): Identifier {
        val token = ctx.Id()
        val text = token.text
        return Identifier(text)
    }

    override fun visitLiteralPrimitive(ctx: LangParser.LiteralPrimitiveContext): Expression {
        val literal = ctx.literal()
        return literalVisitor.visit(literal)
    }

    override fun visitGroupPrimitive(ctx: LangParser.GroupPrimitiveContext): Expression {
        val groupContext = ctx.group()
        val expressionContext = groupContext.expression()
        val expression = expressionVisitor.visitExpression(expressionContext)
        return GroupExpression(expression)
    }

    override fun
}