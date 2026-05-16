package me.eriknikli.rhenium.ast.visitors.expressions

import me.eriknikli.rhenium.ast.tree.expressions.Expression
import me.eriknikli.rhenium.ast.tree.expressions.Identifier
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

}