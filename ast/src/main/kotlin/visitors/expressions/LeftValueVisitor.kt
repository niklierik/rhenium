package me.eriknikli.rhenium.ast.visitors.expressions

import arrow.core.leftNel
import arrow.core.right
import dagger.Lazy
import me.eriknikli.rhenium.ast.diagnostics.UnhandledParseRule
import me.eriknikli.rhenium.ast.tree.expressions.LeftValue
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.parser.RheniumParser
import me.eriknikli.rhenium.parser.RheniumParserBaseVisitor
import javax.inject.Inject
import javax.inject.Singleton

interface ILeftValueVisitor {
    fun visitLeftValue(ctx: RheniumParser.LeftValueContext): Diagnosed<LeftValue>
}

@Singleton
class LeftValueVisitor
@Inject constructor() : RheniumParserBaseVisitor<Diagnosed<LeftValue>>(), ILeftValueVisitor {
    @Inject
    lateinit var expressionVisitor: Lazy<IExpressionVisitor>

    override fun defaultResult(): Diagnosed<LeftValue> = UnhandledParseRule.leftNel()

    override fun visitLeftValue(ctx: RheniumParser.LeftValueContext): Diagnosed<LeftValue> {
        return visit(ctx.identifier())
    }

    override fun visitIdentifier(ctx: RheniumParser.IdentifierContext): Diagnosed<LeftValue> {
        return expressionVisitor.get().identifierOf(ctx).right()
    }
}
