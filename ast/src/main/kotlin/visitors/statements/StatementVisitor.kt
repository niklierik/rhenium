package me.eriknikli.rhenium.ast.visitors.statements

import arrow.core.leftNel
import arrow.core.right
import arrow.core.raise.either
import arrow.core.raise.mapOrAccumulate
import arrow.core.raise.zipOrAccumulate
import dagger.Lazy
import me.eriknikli.rhenium.ast.diagnostics.UnhandledParseRule
import me.eriknikli.rhenium.ast.tree.expressions.operators.Operator
import me.eriknikli.rhenium.ast.tree.statements.BlockStatement
import me.eriknikli.rhenium.ast.tree.statements.ElseBranch
import me.eriknikli.rhenium.ast.tree.statements.ExpressionStatement
import me.eriknikli.rhenium.ast.tree.statements.IfStatement
import me.eriknikli.rhenium.ast.tree.statements.PrintStatement
import me.eriknikli.rhenium.ast.tree.statements.Statement
import me.eriknikli.rhenium.ast.tree.statements.WhileStatement
import me.eriknikli.rhenium.ast.tree.statements.vars.CompoundAssignmentStatement
import me.eriknikli.rhenium.ast.tree.statements.vars.VarAssignmentStatement
import me.eriknikli.rhenium.ast.tree.statements.vars.VarDeclarationStatement
import me.eriknikli.rhenium.ast.visitors.expressions.IExpressionVisitor
import me.eriknikli.rhenium.ast.visitors.expressions.ILeftValueVisitor
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.parser.RheniumParser
import me.eriknikli.rhenium.parser.RheniumParserBaseVisitor
import org.antlr.v4.runtime.Token
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

    override fun visitCompoundAssignmentStatement(
        ctx: RheniumParser.CompoundAssignmentStatementContext
    ): Diagnosed<Statement> = either {
        zipOrAccumulate(
            { leftValueVisitor.get().visitLeftValue(ctx.leftValue()).bindNel() },
            { expressionVisitor.get().visitExpression(ctx.expression()).bindNel() }
        ) { leftValue, rightValue ->
            CompoundAssignmentStatement(ctx, leftValue, compoundOperatorOf(ctx.op), rightValue)
        }
    }

    private fun compoundOperatorOf(token: Token): Operator = when (token.type) {
        RheniumParser.PLUS_EQUALS -> Operator.PLUS
        RheniumParser.MINUS_EQUALS -> Operator.MINUS
        RheniumParser.STAR_EQUALS -> Operator.STAR
        RheniumParser.SLASH_EQUALS -> Operator.SLASH
        RheniumParser.PERCENT_EQUALS -> Operator.PERCENT
        RheniumParser.AND_EQUALS -> Operator.AND
        RheniumParser.OR_EQUALS -> Operator.OR
        else -> throw IllegalStateException("Unhandled compound assignment operator '${token.text}'.")
    }

    override fun visitExpressionStatement(
        ctx: RheniumParser.ExpressionStatementContext
    ): Diagnosed<Statement> = expressionVisitor.get()
        .visitExpression(ctx.expression())
        .map { ExpressionStatement(ctx, it) }

    override fun visitBlock(
        ctx: RheniumParser.BlockContext
    ): Diagnosed<Statement> = blockStatementOf(ctx)

    override fun visitWhileStatement(
        ctx: RheniumParser.WhileStatementContext
    ): Diagnosed<Statement> = either {
        zipOrAccumulate(
            { expressionVisitor.get().visitExpression(ctx.condition).bindNel() },
            { blockStatementOf(ctx.body).bindNel() }
        ) { condition, body ->
            WhileStatement(ctx, condition, body)
        }
    }

    override fun visitIfStatement(
        ctx: RheniumParser.IfStatementContext
    ): Diagnosed<Statement> = ifStatementOf(ctx)

    private fun ifStatementOf(
        ctx: RheniumParser.IfStatementContext
    ): Diagnosed<IfStatement> = either {
        zipOrAccumulate(
            { expressionVisitor.get().visitExpression(ctx.condition).bindNel() },
            { blockStatementOf(ctx.thenBranch).bindNel() },
            { elseBranchOf(ctx)?.bindNel() }
        ) { condition, thenBranch, elseBranch ->
            IfStatement(ctx, condition, thenBranch, elseBranch)
        }
    }

    private fun elseBranchOf(
        ctx: RheniumParser.IfStatementContext
    ): Diagnosed<ElseBranch>? =
        ctx.elseIf?.let { ifStatementOf(it) }
            ?: ctx.elseBlock?.let { blockStatementOf(it) }

    private fun blockStatementOf(
        ctx: RheniumParser.BlockContext
    ): Diagnosed<BlockStatement> = either {
        val statements = mapOrAccumulate(ctx.statement()) { visitStatement(it).bindNel() }

        BlockStatement(ctx, statements)
    }

    override fun visitPrintStatement(
        ctx: RheniumParser.PrintStatementContext
    ): Diagnosed<Statement> {
        val newLine = ctx.PRINTLN() != null
        val expressionNode = ctx.expression()
            ?: return PrintStatement(ctx, newLine, null).right()

        return expressionVisitor.get()
            .visitExpression(expressionNode)
            .map { PrintStatement(ctx, newLine, it) }
    }
}
