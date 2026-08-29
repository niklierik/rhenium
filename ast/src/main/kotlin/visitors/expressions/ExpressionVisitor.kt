package me.eriknikli.rhenium.ast.visitors.expressions

import arrow.core.leftNel
import arrow.core.raise.either
import arrow.core.raise.zipOrAccumulate
import arrow.core.right
import me.eriknikli.rhenium.ast.diagnostics.UnhandledParseRule
import me.eriknikli.rhenium.ast.tree.expressions.Expression
import me.eriknikli.rhenium.ast.tree.expressions.Identifier
import me.eriknikli.rhenium.ast.tree.expressions.operators.BinaryOpExpression
import me.eriknikli.rhenium.ast.tree.expressions.operators.Operator
import me.eriknikli.rhenium.ast.tree.expressions.operators.UnaryOpExpression
import me.eriknikli.rhenium.ast.visitors.expressions.literals.ILiteralTypeVisitor
import me.eriknikli.rhenium.ast.visitors.expressions.literals.ILiteralVisitor
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.parser.RheniumParser
import me.eriknikli.rhenium.parser.RheniumParserBaseVisitor
import javax.inject.Inject
import javax.inject.Singleton

interface IExpressionVisitor {
    fun visitExpression(ctx: RheniumParser.ExpressionContext): Diagnosed<Expression>

    fun identifierOf(ctx: RheniumParser.IdentifierContext): Identifier

    fun typeNameOf(ctx: RheniumParser.TypeNameContext): Diagnosed<Identifier>
}

@Singleton
class ExpressionVisitor
@Inject constructor() : RheniumParserBaseVisitor<Diagnosed<Expression>>(), IExpressionVisitor {

    @Inject
    lateinit var literalVisitor: ILiteralVisitor

    @Inject
    lateinit var literalTypeVisitor: ILiteralTypeVisitor

    override fun defaultResult(): Diagnosed<Expression> = UnhandledParseRule.leftNel()

    override fun visitExpression(ctx: RheniumParser.ExpressionContext): Diagnosed<Expression> {
        return visit(ctx)
    }

    override fun typeNameOf(ctx: RheniumParser.TypeNameContext): Diagnosed<Identifier> {
        ctx.identifier()?.let { return identifierOf(it).right() }
        ctx.signedTypes()?.let { return literalTypeVisitor.visitSignedTypes(it).map { type -> Identifier(ctx, type.name) } }
        ctx.unsignedTypes()?.let { return literalTypeVisitor.visitUnsignedTypes(it).map { type -> Identifier(ctx, type.name) } }
        ctx.floatTypes()?.let { return literalTypeVisitor.visitFloatTypes(it).map { type -> Identifier(ctx, type.name) } }

        throw IllegalStateException("Type name '${ctx.text}' matched no alternative of the typeName rule.")
    }

    override fun identifierOf(ctx: RheniumParser.IdentifierContext): Identifier {
        return Identifier(ctx, ctx.ID().text)
    }

    override fun visitIdentifier(ctx: RheniumParser.IdentifierContext): Diagnosed<Expression> {
        return identifierOf(ctx).right()
    }

    override fun visitLiteralPrimitive(ctx: RheniumParser.LiteralPrimitiveContext): Diagnosed<Expression> {
        return visit(ctx.literal())
    }

    override fun visitGroupPrimitive(ctx: RheniumParser.GroupPrimitiveContext): Diagnosed<Expression> {
        return visit(ctx.expression())
    }

    override fun visitLiteral(ctx: RheniumParser.LiteralContext): Diagnosed<Expression> {
        return literalVisitor.visitLiteral(ctx)
    }

    override fun visitMulExp(ctx: RheniumParser.MulExpContext): Diagnosed<Expression> {
        val op = when (ctx.op.text) {
            "*" -> Operator.STAR
            "/" -> Operator.SLASH
            "%" -> Operator.PERCENT
            else -> Operator.PERCENT
        }

        return binaryOf(ctx, ctx.left, op, ctx.right)
    }

    override fun visitAddExp(ctx: RheniumParser.AddExpContext): Diagnosed<Expression> {
        val op = if (ctx.PLUS() != null) Operator.PLUS else Operator.MINUS

        return binaryOf(ctx, ctx.left, op, ctx.right)
    }

    override fun visitRelationalExp(ctx: RheniumParser.RelationalExpContext): Diagnosed<Expression> {
        val op = when (ctx.op.text) {
            "<" -> Operator.LESS
            "<=" -> Operator.LESS_EQUALS
            ">" -> Operator.GREATER
            ">=" -> Operator.GREATER_EQUALS
            else -> Operator.LESS
        }

        return binaryOf(ctx, ctx.left, op, ctx.right)
    }

    override fun visitEqualityExp(ctx: RheniumParser.EqualityExpContext): Diagnosed<Expression> {
        val op = when (ctx.op.text) {
            "==" -> Operator.EQUALS
            "!=" -> Operator.NOT_EQUALS
            else -> Operator.EQUALS
        }

        return binaryOf(ctx, ctx.left, op, ctx.right)
    }

    override fun visitLogicalExp(ctx: RheniumParser.LogicalExpContext): Diagnosed<Expression> {
        val op = when (ctx.op.text) {
            "&&" -> Operator.AND
            "||" -> Operator.OR
            else -> Operator.AND
        }

        return binaryOf(ctx, ctx.left, op, ctx.right)
    }

    override fun visitUnaryExp(ctx: RheniumParser.UnaryExpContext): Diagnosed<Expression> {
        val op = when (ctx.op.text) {
            "+" -> Operator.PLUS
            "-" -> Operator.MINUS
            "!" -> Operator.BANG
            else -> Operator.BANG
        }

        return visit(ctx.expression()).map { UnaryOpExpression(ctx, op, it) }
    }

    private fun binaryOf(
        ctx: RheniumParser.ExpressionContext,
        left: RheniumParser.ExpressionContext,
        operator: Operator,
        right: RheniumParser.ExpressionContext
    ): Diagnosed<Expression> = either {
        zipOrAccumulate(
            { visit(left).bindNel() },
            { visit(right).bindNel() }
        ) { leftExpression, rightExpression ->
            BinaryOpExpression(ctx, leftExpression, operator, rightExpression)
        }
    }
}
