package me.eriknikli.rhenium.ast.visitors.expressions.literals

import me.eriknikli.rhenium.ast.exceptions.UnexpectedPrimitiveTypeException
import me.eriknikli.rhenium.ast.exceptions.UnknownPrimitiveTypeException
import me.eriknikli.rhenium.ast.tree.expressions.literals.LiteralType
import me.eriknikli.rhenium.common.throwInsteadIf
import me.eriknikli.rhenium.parser.RheniumParser
import me.eriknikli.rhenium.parser.RheniumParserBaseVisitor
import org.antlr.v4.runtime.ParserRuleContext
import javax.inject.Inject
import javax.inject.Singleton

interface ILiteralTypeVisitor {
    fun visitUnsignedTypes(ctx: RheniumParser.UnsignedTypesContext): LiteralType
    fun visitSignedTypes(ctx: RheniumParser.SignedTypesContext): LiteralType
    fun visitFloatTypes(ctx: RheniumParser.FloatTypesContext): LiteralType

}

@Singleton
class LiteralTypeVisitor
@Inject
constructor() : RheniumParserBaseVisitor<LiteralType>(), ILiteralTypeVisitor {
    override fun visitUnsignedTypes(ctx: RheniumParser.UnsignedTypesContext): LiteralType {
        return getType(ctx, LiteralType.U8, LiteralType.U16, LiteralType.U32, LiteralType.U64)
    }

    override fun visitSignedTypes(ctx: RheniumParser.SignedTypesContext): LiteralType {
        return getType(ctx, LiteralType.I8, LiteralType.I16, LiteralType.I32, LiteralType.I64)
    }

    override fun visitFloatTypes(ctx: RheniumParser.FloatTypesContext): LiteralType {
        return getType(ctx, LiteralType.F32, LiteralType.F64)
    }

    private fun getType(ctx: ParserRuleContext, vararg expectedTypes: LiteralType): LiteralType {
        val text = ctx.text
        val parsedType = { LiteralType.valueOf(text.uppercase()) }.throwInsteadIf({ it is IllegalArgumentException }) {
            UnknownPrimitiveTypeException(ctx, text, *expectedTypes)
        }
        if (!expectedTypes.contains(parsedType)) {
            throw UnexpectedPrimitiveTypeException(ctx, parsedType, *expectedTypes)
        }
        return parsedType
    }
}