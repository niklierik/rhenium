package me.eriknikli.rhenium.semanticAnalyzer.statements

import arrow.core.raise.either
import arrow.core.raise.mapOrAccumulate
import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.statements.BlockStatement
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
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

        mapOrAccumulate(statement.statements) {
            statementNodeDecorator.decorateStatement(it, StatementDecoratorContext(blockScope)).bindNel()
        }
    }
}
