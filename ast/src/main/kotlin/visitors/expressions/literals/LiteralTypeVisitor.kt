package me.eriknikli.rhenium.ast.visitors.expressions.literals

import arrow.core.leftNel
import arrow.core.right
import me.eriknikli.rhenium.ast.diagnostics.UnexpectedPrimitiveType
import me.eriknikli.rhenium.ast.diagnostics.UnhandledParseRule
import me.eriknikli.rhenium.ast.diagnostics.UnknownPrimitiveType
import me.eriknikli.rhenium.ast.tree.expressions.literals.LiteralType
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.parser.RheniumParser
import me.eriknikli.rhenium.parser.RheniumParserBaseVisitor
import org.antlr.v4.runtime.ParserRuleContext
import javax.inject.Inject
import javax.inject.Singleton

interface ILiteralTypeVisitor {
    fun visitUnsignedTypes(ctx: RheniumParser.UnsignedTypesContext): Diagnosed<LiteralType>
    fun visitSignedTypes(ctx: RheniumParser.SignedTypesContext): Diagnosed<LiteralType>
    fun visitFloatTypes(ctx: RheniumParser.FloatTypesContext): Diagnosed<LiteralType>
}

@Singleton
class LiteralTypeVisitor
@Inject
constructor() : RheniumParserBaseVisitor<Diagnosed<LiteralType>>(), ILiteralTypeVisitor {
    override fun defaultResult(): Diagnosed<LiteralType> = UnhandledParseRule.leftNel()

    override fun visitUnsignedTypes(ctx: RheniumParser.UnsignedTypesContext): Diagnosed<LiteralType> {
        return getType(ctx, LiteralType.U8, LiteralType.U16, LiteralType.U32, LiteralType.U64)
    }

    override fun visitSignedTypes(ctx: RheniumParser.SignedTypesContext): Diagnosed<LiteralType> {
        return getType(ctx, LiteralType.I8, LiteralType.I16, LiteralType.I32, LiteralType.I64)
    }

    override fun visitFloatTypes(ctx: RheniumParser.FloatTypesContext): Diagnosed<LiteralType> {
        return getType(ctx, LiteralType.F32, LiteralType.F64)
    }

    private fun getType(ctx: ParserRuleContext, vararg expectedTypes: LiteralType): Diagnosed<LiteralType> {
        val text = ctx.text
        val parsedType = try {
            LiteralType.valueOf(text.uppercase())
        } catch (_: IllegalArgumentException) {
            return UnknownPrimitiveType(ctx, text, expectedTypes.toList()).leftNel()
        }

        if (!expectedTypes.contains(parsedType)) {
            return UnexpectedPrimitiveType(ctx, parsedType, expectedTypes.toList()).leftNel()
        }

        return parsedType.right()
    }
}
