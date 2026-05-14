package me.eriknikli.rhenium.ast.visitors.expressions.literals

import me.eriknikli.rhenium.ast.tree.expressions.literals.*
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
            LiteralType.U8 -> U8Literal(text.toUByte())
            LiteralType.U16 -> U16Literal(text.toUShort())
            LiteralType.U32 -> U32Literal(text.toUInt())
            LiteralType.U64 -> U64Literal(text.toULong())
            else -> throw IllegalArgumentException("Type $type is not an unsigned int type.")
        }
    }

    override fun visitSigned(ctx: RheniumParser.SignedContext): Literal<*> {
        val type = literalTypeVisitor.visitSignedTypes(ctx.signedTypes())
        val text = (ctx.SIGNED_INT() ?: ctx.UNSIGNED_INT()).text

        return when (type) {
            LiteralType.I8 -> I8Literal(text.toByte())
            LiteralType.I16 -> I16Literal(text.toShort())
            LiteralType.I32 -> I32Literal(text.toInt())
            LiteralType.I64 -> I64Literal(text.toLong())
            else -> throw IllegalArgumentException("Type $type is not a signed int type.")
        }
    }

    override fun visitFloat(ctx: RheniumParser.FloatContext): Literal<*> {
        val type = literalTypeVisitor.visitFloatTypes(ctx.floatTypes())
        val text = (ctx.FLOAT() ?: ctx.SIGNED_INT() ?: ctx.UNSIGNED_INT()).text

        return when (type) {
            LiteralType.F32 -> F32Literal(text.toFloat())
            LiteralType.F64 -> F64Literal(text.toDouble())
            else -> throw IllegalArgumentException("Type $type is not a float type.")
        }
    }

    override fun visitUnsignedBasic(ctx: RheniumParser.UnsignedBasicContext): Literal<*> {
        val text = ctx.UNSIGNED_INT().text
        return I32Literal(text.toInt())
    }

    override fun visitSignedBasic(ctx: RheniumParser.SignedBasicContext): Literal<*> {
        val text = ctx.SIGNED_INT().text
        return I32Literal(text.toInt())
    }

    override fun visitFloatBasic(ctx: RheniumParser.FloatBasicContext): Literal<*> {
        val text = ctx.FLOAT().text
        return F64Literal(text.toDouble())
    }
}