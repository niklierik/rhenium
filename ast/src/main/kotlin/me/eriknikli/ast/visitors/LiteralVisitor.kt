package me.eriknikli.ast.visitors

import com.google.inject.Inject
import com.google.inject.Singleton
import me.eriknikli.ast.primitives.literals.*
import me.eriknikli.lang.grammar.LangBaseVisitor
import me.eriknikli.lang.grammar.LangParser
import tools.jackson.databind.ObjectMapper

@Singleton
class LiteralVisitor
@Inject constructor(
    private val typedLiteralVisitor: TypedLiteralVisitor,
    private val objectMapper: ObjectMapper
) :
    LangBaseVisitor<Literal<*>>() {

    override fun visitTyped(ctx: LangParser.TypedContext): Literal<*> {
        return typedLiteralVisitor.visit(ctx.typedNumericLiteral())
    }

    override fun visitBool(ctx: LangParser.BoolContext): Literal<*> {
        val token = ctx.BooleanLiteral()
        val text = token.text.lowercase()
        if (text == "true") {
            return TrueLiteral()
        }

        return FalseLiteral()
    }

    override fun visitString(ctx: LangParser.StringContext): Literal<*> {
        val token = ctx.StringLiteral()
        val jsonText = token.text

        val text = objectMapper.readValue(jsonText, String::class.java)

        return StringLiteral(text)
    }

    override fun visitCharacter(ctx: LangParser.CharacterContext): Literal<*> {
        val token = ctx.CharacterLiteral()
        val jsonText = token.text.trim('\'')
        val text = objectMapper.readValue("\"$jsonText\"", String::class.java)
        return CharLiteral(text[0])
    }

    override fun visitReal(ctx: LangParser.RealContext): Literal<*> {
        val token = ctx.FloatingPointLiteral()
        val text = token.text
        val value = text.toDouble()
        return F64Literal(value)
    }

    override fun visitInteger(ctx: LangParser.IntegerContext): Literal<*> {
        val token = ctx.IntegerLiteral()
        val text = token.text
        val value = text.toInt()
        return I32Literal(value)
    }

    override fun visitNull(ctx: LangParser.NullContext?): Literal<*> {
        return NullLiteral()
    }

}