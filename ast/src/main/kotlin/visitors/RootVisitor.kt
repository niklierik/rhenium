package me.eriknikli.rhenium.ast.visitors

import arrow.core.leftNel
import arrow.core.raise.either
import arrow.core.raise.mapOrAccumulate
import arrow.core.raise.zipOrAccumulate
import me.eriknikli.rhenium.ast.diagnostics.UnhandledParseRule
import me.eriknikli.rhenium.ast.tree.Declaration
import me.eriknikli.rhenium.ast.tree.EntryDeclaration
import me.eriknikli.rhenium.ast.tree.FunctionDeclaration
import me.eriknikli.rhenium.ast.tree.Parameter
import me.eriknikli.rhenium.ast.tree.RootNode
import me.eriknikli.rhenium.ast.visitors.expressions.IExpressionVisitor
import me.eriknikli.rhenium.ast.visitors.statements.IStatementVisitor
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.parser.RheniumParser
import me.eriknikli.rhenium.parser.RheniumParserBaseVisitor
import org.antlr.v4.runtime.ParserRuleContext
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

    @Inject
    lateinit var expressionVisitor: IExpressionVisitor

    override fun visitRoot(ctx: RheniumParser.RootContext): Diagnosed<RootNode> = either {
        val declarations = mapOrAccumulate(ctx.children.orEmpty().filterIsInstance<ParserRuleContext>()) {
            declarationOf(it).bindNel()
        }

        RootNode(ctx, declarations)
    }

    private fun declarationOf(ctx: ParserRuleContext): Diagnosed<Declaration> = when (ctx) {
        is RheniumParser.EntryDeclarationContext -> entryOf(ctx)
        is RheniumParser.FunctionDeclarationContext -> functionOf(ctx)
        else -> throw IllegalStateException("Unhandled declaration rule ${ctx.javaClass.simpleName}.")
    }

    private fun entryOf(ctx: RheniumParser.EntryDeclarationContext): Diagnosed<Declaration> =
        statementVisitor.visitBlockStatement(ctx.body).map { EntryDeclaration(ctx, ctx.name.text, it) }

    private fun functionOf(
        ctx: RheniumParser.FunctionDeclarationContext
    ): Diagnosed<Declaration> = either {
        val returnTypeNode: RheniumParser.TypeNameContext? = ctx.returnType

        zipOrAccumulate(
            { mapOrAccumulate(ctx.parameter()) { parameterOf(it).bindNel() } },
            { returnTypeNode?.let { expressionVisitor.typeNameOf(it).bindNel() } },
            { statementVisitor.visitBlockStatement(ctx.body).bindNel() }
        ) { parameters, returnType, body ->
            FunctionDeclaration(ctx, ctx.name.text, parameters, returnType, body)
        }
    }

    private fun parameterOf(ctx: RheniumParser.ParameterContext): Diagnosed<Parameter> =
        expressionVisitor.typeNameOf(ctx.type).map { Parameter(ctx, ctx.CONST() == null, ctx.name.text, it) }
}
