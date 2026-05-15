package me.eriknikli.rhenium.ast.visitors.statements

import me.eriknikli.rhenium.ast.tree.statements.Statement
import me.eriknikli.rhenium.ast.tree.statements.vars.VarDeclarationStatement
import me.eriknikli.rhenium.ast.visitors.expressions.IExpressionVisitor
import me.eriknikli.rhenium.parser.RheniumParser
import me.eriknikli.rhenium.parser.RheniumParserBaseVisitor
import javax.inject.Inject
import javax.inject.Singleton

interface IStatementVisitor {
    fun visitStatement(ctx: RheniumParser.StatementContext): Statement
}

@Singleton
class StatementVisitor
@Inject constructor() : RheniumParserBaseVisitor<Statement>(

), IStatementVisitor {
    @Inject
    lateinit var expressionVisitor: IExpressionVisitor

    override fun visitVarDeclarationStatement(ctx: RheniumParser.VarDeclarationStatementContext): Statement {
        val name = ctx.ID().text
        val expression = expressionVisitor.visitExpression(ctx.expression())
        return VarDeclarationStatement(true, name, expression)
    }
}