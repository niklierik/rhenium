package me.eriknikli.rhenium.ast.visitors.expressions.literals

import me.eriknikli.rhenium.ast.tree.expressions.literals.LiteralType
import me.eriknikli.rhenium.parser.RheniumParser
import me.eriknikli.rhenium.parser.RheniumParserBaseVisitor
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
        return getType(ctx.text)
    }

    override fun visitSignedTypes(ctx: RheniumParser.SignedTypesContext): LiteralType {
        return getType(ctx.text)
    }

    override fun visitFloatTypes(ctx: RheniumParser.FloatTypesContext): LiteralType {
        return getType(ctx.text)
    }

    private fun getType(text: String): LiteralType {
        return LiteralType.valueOf(text.uppercase())
    }
}