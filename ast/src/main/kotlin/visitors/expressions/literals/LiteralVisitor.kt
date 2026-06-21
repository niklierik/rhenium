package me.eriknikli.rhenium.ast.visitors.expressions.literals

import me.eriknikli.rhenium.ast.exceptions.InvalidValueOfLiteralException
import me.eriknikli.rhenium.ast.exceptions.UnexpectedPrimitiveTypeException
import me.eriknikli.rhenium.ast.tree.expressions.literals.*
import me.eriknikli.rhenium.common.throwInsteadIf
import me.eriknikli.rhenium.parser.RheniumParser
import me.eriknikli.rhenium.parser.RheniumParserBaseVisitor
import javax.inject.Inject
import javax.inject.Singleton

interface ILiteralVisitor {
    fun visitLiteral(ctx: RheniumParser.LiteralContext): Literal<*>
}

@Singleton
class LiteralVisitor
@Inject
constructor() : RheniumParserBaseVisitor<Literal<*>>(), ILiteralVisitor {

    @Inject
    lateinit var literalTypeVisitor: ILiteralTypeVisitor

    override fun visitUnsigned(ctx: RheniumParser.UnsignedContext): Literal<*> {
        val type = literalTypeVisitor.visitUnsignedTypes(ctx.unsignedTypes())
        val text = ctx.UNSIGNED_INT().text
        return when (type) {
            LiteralType.U8 -> U8Literal(ctx, text.toUByte(), text)
            LiteralType.U16 -> U16Literal(ctx, text.toUShort(), text)
            LiteralType.U32 -> U32Literal(ctx, text.toUInt(), text)
            LiteralType.U64 -> U64Literal(ctx, text.toULong(), text)
            else -> throw UnexpectedPrimitiveTypeException(
                ctx,
                type,
                LiteralType.U8,
                LiteralType.U16,
                LiteralType.U32,
                LiteralType.U64
            )
        }
    }

    override fun visitSigned(ctx: RheniumParser.SignedContext): Literal<*> {
        val type = literalTypeVisitor.visitSignedTypes(ctx.signedTypes())
        val text = (ctx.SIGNED_INT() ?: ctx.UNSIGNED_INT()).text

        return when (type) {
            LiteralType.I8 -> I8Literal(ctx, text.toByte(), text)
            LiteralType.I16 -> I16Literal(ctx, text.toShort(), text)
            LiteralType.I32 -> I32Literal(ctx, text.toInt(), text)
            LiteralType.I64 -> I64Literal(ctx, text.toLong(), text)
            else -> throw UnexpectedPrimitiveTypeException(
                ctx,
                type,
                LiteralType.I8,
                LiteralType.I16,
                LiteralType.I32,
                LiteralType.I64
            )
        }
    }

    override fun visitFloat(ctx: RheniumParser.FloatContext): Literal<*> {
        val type = literalTypeVisitor.visitFloatTypes(ctx.floatTypes())
        val text = (ctx.FLOAT() ?: ctx.SIGNED_INT() ?: ctx.UNSIGNED_INT()).text

        return when (type) {
            LiteralType.F32 -> F32Literal(ctx, text.toFloat(), text)
            LiteralType.F64 -> F64Literal(ctx, text.toDouble(), text)
            else -> throw UnexpectedPrimitiveTypeException(
                ctx,
                type,
                LiteralType.F32,
                LiteralType.F64
            )
        }
    }

    override fun visitUnsignedBasic(ctx: RheniumParser.UnsignedBasicContext): Literal<*> {
        val text = ctx.UNSIGNED_INT().text
        return I32Literal(
            ctx,
            { text.toInt() }.throwInsteadIf({ it is NumberFormatException }) {
                InvalidValueOfLiteralException(
                    ctx,
                    text,
                    LiteralType.I32
                )
            },
            text
        )
    }

    override fun visitSignedBasic(ctx: RheniumParser.SignedBasicContext): Literal<*> {
        val text = ctx.SIGNED_INT().text
        return I32Literal(ctx, { text.toInt() }.throwInsteadIf({ it is NumberFormatException }) {
            InvalidValueOfLiteralException(
                ctx,
                text,
                LiteralType.I32
            )
        }, text)
    }

    override fun visitFloatBasic(ctx: RheniumParser.FloatBasicContext): Literal<*> {
        val text = ctx.FLOAT().text
        return F64Literal(
            ctx,
            { text.toDouble() }.throwInsteadIf({ it is NumberFormatException }) {
                InvalidValueOfLiteralException(
                    ctx,
                    text,
                    LiteralType.F64
                )
            },
            text
        )
    }

    override fun visitBooleanLiteral(ctx: RheniumParser.BooleanLiteralContext): Literal<*> {
        val text = ctx.text

        return when (text) {
            "true" -> BooleanLiteral(ctx, true, "true")
            "false" -> BooleanLiteral(ctx, false, "false")
            else -> throw InvalidValueOfLiteralException(ctx, text, LiteralType.BOOL)
        }
    }
}