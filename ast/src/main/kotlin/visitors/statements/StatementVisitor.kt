package me.eriknikli.rhenium.ast.visitors.statements

import arrow.core.leftNel
import arrow.core.raise.either
import arrow.core.raise.zipOrAccumulate
import dagger.Lazy
import me.eriknikli.rhenium.ast.diagnostics.UnhandledParseRule
import me.eriknikli.rhenium.ast.tree.statements.Statement
import me.eriknikli.rhenium.ast.tree.statements.vars.VarAssignmentStatement
import me.eriknikli.rhenium.ast.tree.statements.vars.VarDeclarationStatement
import me.eriknikli.rhenium.ast.visitors.expressions.IExpressionVisitor
import me.eriknikli.rhenium.ast.visitors.expressions.ILeftValueVisitor
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.parser.RheniumParser
import me.eriknikli.rhenium.parser.RheniumParserBaseVisitor
import javax.inject.Inject
import javax.inject.Singleton

interface IStatementVisitor {
    fun visitStatement(ctx: RheniumParser.StatementContext): Diagnosed<Statement>
}

@Singleton
class StatementVisitor
@Inject constructor() : RheniumParserBaseVisitor<Diagnosed<Statement>>(), IStatementVisitor {
    @Inject
    lateinit var expressionVisitor: Lazy<IExpressionVisitor>

    @Inject
    lateinit var leftValueVisitor: Lazy<ILeftValueVisitor>

    override fun defaultResult(): Diagnosed<Statement> = UnhandledParseRule.leftNel()

    override fun visitVarDeclarationStatement(
        ctx: RheniumParser.VarDeclarationStatementContext
    ): Diagnosed<Statement> = either {
        val mutable = ctx.LET() != null
        val name = ctx.name.text
        val expectedTypeNode: RheniumParser.TypeNameContext? = ctx.expectedType

        zipOrAccumulate(
            { expectedTypeNode?.let { expressionVisitor.get().typeNameOf(it).bindNel() } },
            { expressionVisitor.get().visitExpression(ctx.expression()).bindNel() }
        ) { expectedType, expression ->
            VarDeclarationStatement(ctx, mutable, name, expectedType, expression)
        }
    }

    override fun visitVarAssignmentStatement(
        ctx: RheniumParser.VarAssignmentStatementContext
    ): Diagnosed<Statement> = either {
        zipOrAccumulate(
            { leftValueVisitor.get().visitLeftValue(ctx.leftValue()).bindNel() },
            { expressionVisitor.get().visitExpression(ctx.expression()).bindNel() }
        ) { leftValue, rightValue ->
            VarAssignmentStatement(ctx, leftValue, rightValue)
        }
    }
}
