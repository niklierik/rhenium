package me.eriknikli.rhenium.ast.visitors.expressions

import me.eriknikli.rhenium.ast.tree.expressions.Expression
import me.eriknikli.rhenium.ast.visitors.expressions.literals.ILiteralVisitor
import me.eriknikli.rhenium.parser.RheniumParser
import me.eriknikli.rhenium.parser.RheniumParserBaseVisitor
import javax.inject.Inject
import javax.inject.Singleton

interface IExpressionVisitor {
    fun visitExpression(ctx: RheniumParser.ExpressionContext): Expression
}

@Singleton
class ExpressionVisitor
@Inject constructor() : RheniumParserBaseVisitor<Expression>(), IExpressionVisitor {

    @Inject
    lateinit var literalVisitor: ILiteralVisitor

    override fun visitLiteral(ctx: RheniumParser.LiteralContext): Expression {
        return literalVisitor.visitLiteral(ctx)
    }

}