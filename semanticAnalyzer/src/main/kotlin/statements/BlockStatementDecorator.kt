package me.eriknikli.rhenium.semanticAnalyzer.statements

import arrow.core.nel
import arrow.core.raise.either
import arrow.core.raise.mapOrAccumulate
import arrow.core.raise.zipOrAccumulate
import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.statements.BlockStatement
import me.eriknikli.rhenium.ast.tree.statements.LoopControlStatement
import me.eriknikli.rhenium.ast.tree.statements.Statement
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.UnreachableStatement
import me.eriknikli.rhenium.semanticContext.scope.Scope
import javax.inject.Inject
import javax.inject.Singleton

interface IBlockStatementDecorator {
    fun decorate(statement: BlockStatement, context: StatementDecoratorContext): Diagnosed<Unit>
}

@Singleton
class BlockStatementDecorator
@Inject
constructor() : IBlockStatementDecorator {
    @Inject
    lateinit var statementNodeDecoratorProvider: Lazy<IStatementNodeDecorator>

    private val statementNodeDecorator by lazy { statementNodeDecoratorProvider.get() }

    override fun decorate(
        statement: BlockStatement,
        context: StatementDecoratorContext
    ): Diagnosed<Unit> = either {
        val blockScope = context.scope.createChild()

        zipOrAccumulate(
            {
                mapOrAccumulate(statement.statements) {
                    statementNodeDecorator.decorateStatement(it, StatementDecoratorContext(blockScope)).bindNel()
                }
            },
            { checkReachable(statement.statements, blockScope).bindNel() }
        ) { _, _ -> }
    }

    private fun checkReachable(statements: List<Statement>, scope: Scope): Diagnosed<Unit> = either {
        if (!scope.isInsideLoop()) {
            return@either
        }

        val index = statements.indexOfFirst { it is LoopControlStatement }
        val loopControl = statements.getOrNull(index) as? LoopControlStatement ?: return@either
        val unreachable = statements.getOrNull(index + 1) ?: return@either

        raise(UnreachableStatement(unreachable.parserContext, loopControl).nel())
    }
}
