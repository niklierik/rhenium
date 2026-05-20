package me.eriknikli.rhenium.ast.visitors.expressions

import me.eriknikli.rhenium.ast.tree.expressions.Expression
import me.eriknikli.rhenium.ast.tree.expressions.Identifier
import me.eriknikli.rhenium.ast.tree.expressions.operators.BinaryOpExpression
import me.eriknikli.rhenium.ast.tree.expressions.operators.Operator
import me.eriknikli.rhenium.ast.tree.expressions.operators.UnaryOpExpression
import me.eriknikli.rhenium.ast.visitors.expressions.literals.ILiteralTypeVisitor
import me.eriknikli.rhenium.ast.visitors.expressions.literals.ILiteralVisitor
import me.eriknikli.rhenium.parser.RheniumParser
import me.eriknikli.rhenium.parser.RheniumParserBaseVisitor
import javax.inject.Inject
import javax.inject.Singleton

interface IExpressionVisitor {
    fun visitExpression(ctx: RheniumParser.ExpressionContext): Expression
    fun visitIdentifier(ctx: RheniumParser.IdentifierContext): Identifier
    fun visitTypeName(ctx: RheniumParser.TypeNameContext): Identifier
}

@Singleton
class ExpressionVisitor
@Inject constructor() : RheniumParserBaseVisitor<Expression>(), IExpressionVisitor {

    @Inject
    lateinit var literalVisitor: ILiteralVisitor

    @Inject
    lateinit var literalTypeVisitor: ILiteralTypeVisitor

    override fun visitExpression(ctx: RheniumParser.ExpressionContext): Expression {
        return visit(ctx)
    }

    override fun visitTypeName(ctx: RheniumParser.TypeNameContext): Identifier {
        val identifier = ctx.identifier()
        if (identifier != null) {
            return visitIdentifier(identifier)
        }

        val signedType = ctx.signedTypes()?.apply { literalTypeVisitor.visitSignedTypes(this) }
        if (signedType != null) {
            return Identifier(signedType.text)
        }

        val unsignedType = ctx.unsignedTypes()?.apply { literalTypeVisitor.visitUnsignedTypes(this) }
        if (unsignedType != null) {
            return Identifier(unsignedType.text)
        }

        val floatType = ctx.floatTypes()?.apply { literalTypeVisitor.visitFloatTypes(this) }
        if (floatType != null) {
            return Identifier(floatType.text)
        }

        throw IllegalStateException("Unhandled type name state.")
    }

    override fun visitIdentifier(ctx: RheniumParser.IdentifierContext): Identifier {
        val name = ctx.ID().text
        return Identifier(name)
    }

    override fun visitLiteralPrimitive(ctx: RheniumParser.LiteralPrimitiveContext): Expression {
        return visit(ctx.literal())
    }

    override fun visitGroupPrimitive(ctx: RheniumParser.GroupPrimitiveContext): Expression {
        return visit(ctx.expression())
    }

    override fun visitLiteral(ctx: RheniumParser.LiteralContext): Expression {
        return literalVisitor.visitLiteral(ctx)
    }

    override fun visitMulExp(ctx: RheniumParser.MulExpContext): BinaryOpExpression {
        val left = visit(ctx.left)
        val right = visit(ctx.right)
        val opText = ctx.op.text

        val op = when (opText) {
            "*" -> Operator.STAR
            "/" -> Operator.SLASH
            else -> Operator.PERCENT
        }

        return BinaryOpExpression(left, op, right)
    }

    override fun visitAddExp(ctx: RheniumParser.AddExpContext): BinaryOpExpression {
        val left = visit(ctx.left)
        val right = visit(ctx.right)
        val isPlus = ctx.PLUS() != null
        val op = if (isPlus) Operator.PLUS else Operator.MINUS

        return BinaryOpExpression(left, op, right)
    }

    override fun visitRelationalExp(ctx: RheniumParser.RelationalExpContext): Expression {
        val left = visit(ctx.left)
        val right = visit(ctx.right)
        val op = when (ctx.text) {
            "<" -> Operator.LESS
            "<=" -> Operator.LESS_EQUALS
            ">" -> Operator.GREATER
            ">=" -> Operator.GREATER_EQUALS
            else -> Operator.LESS
        }
        return BinaryOpExpression(left, op, right)
    }

    override fun visitEqualityExp(ctx: RheniumParser.EqualityExpContext): Expression {
        val left = visit(ctx.left)
        val right = visit(ctx.right)
        val op = when (ctx.text) {
            "==" -> Operator.EQUALS
            "!=" -> Operator.NOT_EQUALS
            else -> Operator.EQUALS
        }
        return BinaryOpExpression(left, op, right)
    }

    override fun visitLogicalExp(ctx: RheniumParser.LogicalExpContext): Expression {
        val left = visit(ctx.left)
        val right = visit(ctx.right)
        val op = when (ctx.op.text) {
            "&&" -> Operator.AND
            "||" -> Operator.OR
            else -> Operator.AND
        }
        return BinaryOpExpression(left, op, right)
    }

    override fun visitUnaryExp(ctx: RheniumParser.UnaryExpContext): Expression {
        val expression = visit(ctx.expression())
        val op = when (ctx.op.text) {
            "+" -> Operator.PLUS
            "-" -> Operator.MINUS
            "!" -> Operator.BANG
            else -> Operator.BANG
        }
        return UnaryOpExpression(op, expression)
    }



}