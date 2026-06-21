package me.eriknikli.rhenium.ast.visitors.statements

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.statements.Statement
import me.eriknikli.rhenium.ast.tree.statements.vars.VarAssignmentStatement
import me.eriknikli.rhenium.ast.tree.statements.vars.VarDeclarationStatement
import me.eriknikli.rhenium.ast.visitors.expressions.IExpressionVisitor
import me.eriknikli.rhenium.ast.visitors.expressions.ILeftValueVisitor
import me.eriknikli.rhenium.common.and
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
    lateinit var expressionVisitor: Lazy<IExpressionVisitor>

    @Inject
    lateinit var leftValueVisitor: Lazy<ILeftValueVisitor>

    override fun visitVarDeclarationStatement(ctx: RheniumParser.VarDeclarationStatementContext): Statement {
        val mutable = ctx.LET() != null

        val name = ctx.name.text

        val expectedTypeNode = try {
            ctx.expectedType
        } catch (_: NullPointerException) {
            null
        }

        val (expectedType, expression) = and(
            {
                expectedTypeNode?.let { expressionVisitor.get().visitTypeName(it) }
            },
            {
                expressionVisitor.get().visitExpression(ctx.expression())
            }
        )


        return VarDeclarationStatement(ctx, mutable, name, expectedType, expression)
    }

    override fun visitVarAssignmentStatement(ctx: RheniumParser.VarAssignmentStatementContext): Statement {

        val leftValue = leftValueVisitor.get().visitLeftValue(ctx.leftValue())
        val rightValue = expressionVisitor.get().visitExpression(ctx.expression())

        return VarAssignmentStatement(ctx, leftValue, rightValue)
    }
}