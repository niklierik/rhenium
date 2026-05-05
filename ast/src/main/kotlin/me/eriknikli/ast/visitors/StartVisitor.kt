package me.eriknikli.ast.visitors

import com.google.inject.Inject
import com.google.inject.Singleton
import me.eriknikli.ast.Expression
import me.eriknikli.lang.grammar.LangBaseVisitor
import me.eriknikli.lang.grammar.LangParser

@Singleton
class StartVisitor
@Inject constructor(
    private val literalVisitor: LiteralVisitor
) : LangBaseVisitor<Expression>() {

    override fun visitStart(ctx: LangParser.StartContext): Expression {
        val primitive = ctx.primitive()
        return literalVisitor.visit(primitive)
    }

}