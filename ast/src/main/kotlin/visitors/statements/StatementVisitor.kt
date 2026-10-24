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
import me.eriknikli.rhenium.ast.tree.statements.BreakStatement
import me.eriknikli.rhenium.ast.tree.statements.ContinueStatement
import me.eriknikli.rhenium.ast.tree.statements.ElseBranch
import me.eriknikli.rhenium.ast.tree.statements.ExpressionStatement
import me.eriknikli.rhenium.ast.tree.statements.ForStatement
import me.eriknikli.rhenium.ast.tree.statements.IfStatement
import me.eriknikli.rhenium.ast.tree.statements.IterationExpression
import me.eriknikli.rhenium.ast.tree.statements.PrintStatement
import me.eriknikli.rhenium.ast.tree.statements.ReturnStatement
import me.eriknikli.rhenium.ast.tree.statements.Statement
import me.eriknikli.rhenium.ast.tree.statements.WhileStatement
import me.eriknikli.rhenium.ast.tree.statements.vars.CompoundAssignmentStatement
import me.eriknikli.rhenium.ast.tree.statements.vars.IncrementStatement
import me.eriknikli.rhenium.ast.tree.statements.vars.Step
import me.eriknikli.rhenium.ast.tree.statements.vars.VarAssignmentStatement
import me.eriknikli.rhenium.ast.tree.statements.vars.VarDeclarationStatement
import me.eriknikli.rhenium.ast.visitors.expressions.IExpressionVisitor
import me.eriknikli.rhenium.ast.visitors.expressions.ILeftValueVisitor
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.parser.RheniumParser
import me.eriknikli.rhenium.parser.RheniumParserBaseVisitor
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.Token
import javax.inject.Inject
import javax.inject.Singleton

interface IStatementVisitor {
    fun visitStatement(ctx: RheniumParser.StatementContext): Diagnosed<Statement>

    fun visitBlockStatement(ctx: RheniumParser.BlockContext): Diagnosed<BlockStatement>
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
    ): Diagnosed<Statement> = varAssignmentOf(ctx.varAssignment())

    private fun varAssignmentOf(
        ctx: RheniumParser.VarAssignmentContext
    ): Diagnosed<VarAssignmentStatement> = either {
        zipOrAccumulate(
            { leftValueVisitor.get().visitLeftValue(ctx.leftValue()).bindNel() },
            { expressionVisitor.get().visitExpression(ctx.expression()).bindNel() }
        ) { leftValue, rightValue ->
            VarAssignmentStatement(ctx, leftValue, rightValue)
        }
    }

    override fun visitCompoundAssignmentStatement(
        ctx: RheniumParser.CompoundAssignmentStatementContext
    ): Diagnosed<Statement> = compoundAssignmentOf(ctx.compoundAssignment())

    private fun compoundAssignmentOf(
        ctx: RheniumParser.CompoundAssignmentContext
    ): Diagnosed<CompoundAssignmentStatement> = either {
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

    override fun visitIncrementStatement(
        ctx: RheniumParser.IncrementStatementContext
    ): Diagnosed<Statement> = incrementOf(ctx.increment())

    private fun incrementOf(
        ctx: RheniumParser.IncrementContext
    ): Diagnosed<IncrementStatement> = leftValueVisitor.get()
        .visitLeftValue(ctx.leftValue())
        .map { IncrementStatement(ctx, it, stepOf(ctx.op)) }

    private fun stepOf(token: Token): Step = when (token.type) {
        RheniumParser.PLUSPLUS -> Step.UP
        RheniumParser.MINUSMINUS -> Step.DOWN
        else -> throw IllegalStateException("Unhandled increment operator '${token.text}'.")
    }

    override fun visitExpressionStatement(
        ctx: RheniumParser.ExpressionStatementContext
    ): Diagnosed<Statement> = expressionStatementOf(ctx, ctx.expression())

    private fun expressionStatementOf(
        ctx: ParserRuleContext,
        expression: RheniumParser.ExpressionContext
    ): Diagnosed<ExpressionStatement> = expressionVisitor.get()
        .visitExpression(expression)
        .map { ExpressionStatement(ctx, it) }

    override fun visitBlock(
        ctx: RheniumParser.BlockContext
    ): Diagnosed<Statement> = blockStatementOf(ctx)

    override fun visitBlockStatement(
        ctx: RheniumParser.BlockContext
    ): Diagnosed<BlockStatement> = blockStatementOf(ctx)

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

    override fun visitForStatement(
        ctx: RheniumParser.ForStatementContext
    ): Diagnosed<Statement> = either {
        zipOrAccumulate(
            { initClauseOf(ctx.initClause()).bindNel() },
            { expressionVisitor.get().visitExpression(ctx.condition).bindNel() },
            { iterationExpressionOf(ctx.iterationExpression()).bindNel() },
            { blockStatementOf(ctx.body).bindNel() }
        ) { initClause, condition, iterationExpression, body ->
            ForStatement(ctx, initClause, condition, iterationExpression, body)
        }
    }

    private fun initClauseOf(
        ctx: RheniumParser.InitClauseContext
    ): Diagnosed<Statement> =
        ctx.varDeclarationStatement()?.let { visitVarDeclarationStatement(it) }
            ?: ctx.varAssignmentStatement()?.let { visitVarAssignmentStatement(it) }
            ?: ctx.compoundAssignmentStatement()?.let { visitCompoundAssignmentStatement(it) }
            ?: ctx.incrementStatement()?.let { visitIncrementStatement(it) }
            ?: visitExpressionStatement(ctx.expressionStatement())

    private fun iterationExpressionOf(
        ctx: RheniumParser.IterationExpressionContext
    ): Diagnosed<IterationExpression> =
        ctx.varAssignment()?.let { varAssignmentOf(it) }
            ?: ctx.compoundAssignment()?.let { compoundAssignmentOf(it) }
            ?: ctx.increment()?.let { incrementOf(it) }
            ?: expressionStatementOf(ctx, ctx.expression())

    override fun visitBreakStatement(
        ctx: RheniumParser.BreakStatementContext
    ): Diagnosed<Statement> = BreakStatement(ctx).right()

    override fun visitContinueStatement(
        ctx: RheniumParser.ContinueStatementContext
    ): Diagnosed<Statement> = ContinueStatement(ctx).right()

    override fun visitReturnStatement(
        ctx: RheniumParser.ReturnStatementContext
    ): Diagnosed<Statement> {
        val valueNode = ctx.expression() ?: return ReturnStatement(ctx, null).right()

        return expressionVisitor.get()
            .visitExpression(valueNode)
            .map { ReturnStatement(ctx, it) }
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
