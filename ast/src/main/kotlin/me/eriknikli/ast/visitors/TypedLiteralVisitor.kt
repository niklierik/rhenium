package me.eriknikli.ast.visitors

import com.google.inject.Inject
import com.google.inject.Singleton
import me.eriknikli.ast.primitives.literals.*
import me.eriknikli.lang.grammar.LangBaseVisitor
import me.eriknikli.lang.grammar.LangParser

@Singleton
class TypedLiteralVisitor
@Inject constructor() : LangBaseVisitor<Literal<*>>() {
    override fun visitF64(ctx: LangParser.F64Context): F64Literal {
        val token = ctx.FloatingPointLiteral()
        val text = token.text
        val value = text.toDouble()
        return F64Literal(value)
    }

    override fun visitF32(ctx: LangParser.F32Context): F32Literal {
        val token = ctx.FloatingPointLiteral()
        val text = token.text
        val value = text.toFloat()
        return F32Literal(value)
    }

    override fun visitU64(ctx: LangParser.U64Context): U64Literal {
        val token = ctx.IntegerLiteral()
        val text = token.text
        val value = text.toULong()
        return U64Literal(value)
    }

    override fun visitU32(ctx: LangParser.U32Context): U32Literal {
        val token = ctx.IntegerLiteral()
        val text = token.text
        val value = text.toUInt()
        return U32Literal(value)
    }

    override fun visitU16(ctx: LangParser.U16Context): U16Literal {
        val token = ctx.IntegerLiteral()
        val text = token.text
        val value = text.toUShort()
        return U16Literal(value)
    }

    override fun visitU8(ctx: LangParser.U8Context): U8Literal {
        val token = ctx.IntegerLiteral()
        val text = token.text
        val value = text.toUByte()
        return U8Literal(value)
    }

    override fun visitI64(ctx: LangParser.I64Context): I64Literal {
        val token = ctx.IntegerLiteral()
        val text = token.text
        val value = text.toLong()
        return I64Literal(value)
    }

    override fun visitI32(ctx: LangParser.I32Context): I32Literal {
        val token = ctx.IntegerLiteral()
        val text = token.text
        val value = text.toInt()
        return I32Literal(value)
    }

    override fun visitI16(ctx: LangParser.I16Context): I16Literal {
        val token = ctx.IntegerLiteral()
        val text = token.text
        val value = text.toShort()
        return I16Literal(value)
    }

    override fun visitI8(ctx: LangParser.I8Context): I8Literal {
        val token = ctx.IntegerLiteral()
        val text = token.text
        val value = text.toByte()
        return I8Literal(value)
    }
}