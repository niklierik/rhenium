package me.eriknikli.rhenium.ast.visitors

import arrow.core.leftNel
import arrow.core.raise.either
import arrow.core.raise.mapOrAccumulate
import me.eriknikli.rhenium.ast.diagnostics.UnhandledParseRule
import me.eriknikli.rhenium.ast.tree.RootNode
import me.eriknikli.rhenium.ast.visitors.statements.IStatementVisitor
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.parser.RheniumParser
import me.eriknikli.rhenium.parser.RheniumParserBaseVisitor
import javax.inject.Inject
import javax.inject.Singleton

interface IRootVisitor {
    fun visitRoot(ctx: RheniumParser.RootContext): Diagnosed<RootNode>
}

@Singleton
class RootVisitor
@Inject
constructor() : RheniumParserBaseVisitor<Diagnosed<RootNode>>(), IRootVisitor {
    @Inject
    lateinit var statementVisitor: IStatementVisitor

    override fun defaultResult(): Diagnosed<RootNode> = UnhandledParseRule.leftNel()

    override fun visitRoot(ctx: RheniumParser.RootContext): Diagnosed<RootNode> = either {
        val statements = mapOrAccumulate(ctx.statement()) { statementVisitor.visitStatement(it).bindNel() }

        RootNode(ctx, statements)
    }
}
