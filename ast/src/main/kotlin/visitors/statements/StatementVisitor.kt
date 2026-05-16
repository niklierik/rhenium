package me.eriknikli.rhenium.ast.visitors.statements

import me.eriknikli.rhenium.ast.tree.expressions.Identifier
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
        val mutable = ctx.LET() != null

        val name = ctx.name.text

        val expectedTypeNode = ctx.expectedType
        var expectedType: Identifier? = null
        if (expectedTypeNode != null) {
            expectedType = expressionVisitor.visitTypeName(expectedTypeNode)
        }

        val expression = expressionVisitor.visitExpression(ctx.expression())

        return VarDeclarationStatement(mutable, name, expectedType, expression)
    }
}