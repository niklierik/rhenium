package me.eriknikli.rhenium.ast.visitors

import me.eriknikli.rhenium.ast.tree.RootNode
import me.eriknikli.rhenium.ast.visitors.expressions.literals.ILiteralVisitor
import me.eriknikli.rhenium.ast.visitors.statements.IStatementVisitor
import me.eriknikli.rhenium.parser.RheniumParser
import me.eriknikli.rhenium.parser.RheniumParserBaseVisitor
import javax.inject.Inject
import javax.inject.Singleton

interface IRootVisitor {
    fun visitRoot(ctx: RheniumParser.RootContext): RootNode
}

@Singleton
class RootVisitor
@Inject
constructor() : RheniumParserBaseVisitor<RootNode>(), IRootVisitor {
    @Inject
    lateinit var literalVisitor: ILiteralVisitor

    @Inject
    lateinit var statementVisitor: IStatementVisitor

    override fun visitRoot(ctx: RheniumParser.RootContext): RootNode {
        val statementNodes = ctx.statement().map { statementVisitor.visitStatement(it) }.toList()
        return RootNode(statementNodes)
    }
}