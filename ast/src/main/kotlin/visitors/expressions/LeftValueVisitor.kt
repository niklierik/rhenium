package me.eriknikli.rhenium.ast.visitors.expressions

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.expressions.LeftValue
import me.eriknikli.rhenium.parser.RheniumParser
import me.eriknikli.rhenium.parser.RheniumParserBaseVisitor
import javax.inject.Inject
import javax.inject.Singleton

interface ILeftValueVisitor {
    fun visitLeftValue(ctx: RheniumParser.LeftValueContext): LeftValue
}

@Singleton
class LeftValueVisitor
@Inject constructor() : RheniumParserBaseVisitor<LeftValue>(), ILeftValueVisitor {
    @Inject
    lateinit var expressionVisitor: Lazy<ExpressionVisitor>

    override fun visitLeftValue(ctx: RheniumParser.LeftValueContext): LeftValue {
        return visit(ctx.identifier())
    }

    override fun visitIdentifier(ctx: RheniumParser.IdentifierContext): LeftValue {
        return expressionVisitor.get().visitIdentifier(ctx)
    }

}