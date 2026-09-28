package me.eriknikli.rhenium.semanticAnalyzer.statements

import arrow.core.raise.either
import arrow.core.raise.zipOrAccumulate
import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.statements.IfStatement
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import javax.inject.Inject
import javax.inject.Singleton

interface IIfStatementDecorator {
    fun decorate(statement: IfStatement, context: StatementDecoratorContext): Diagnosed<Unit>
}

@Singleton
class IfStatementDecorator
@Inject
constructor() : IIfStatementDecorator {
    @Inject
    lateinit var conditionDecoratorProvider: Lazy<IConditionDecorator>

    @Inject
    lateinit var statementNodeDecoratorProvider: Lazy<IStatementNodeDecorator>

    private val conditionDecorator by lazy { conditionDecoratorProvider.get() }
    private val statementNodeDecorator by lazy { statementNodeDecoratorProvider.get() }

    override fun decorate(
        statement: IfStatement,
        context: StatementDecoratorContext
    ): Diagnosed<Unit> = either {
        zipOrAccumulate(
            {
                conditionDecorator
                    .decorate(statement.condition, StatementDecoratorContext(context.scope))
                    .bindNel()
            },
            {
                statementNodeDecorator
                    .decorateStatement(statement.thenBranch, StatementDecoratorContext(context.scope))
                    .bindNel()
            },
            {
                statement.elseBranch?.let {
                    statementNodeDecorator
                        .decorateStatement(it, StatementDecoratorContext(context.scope))
                        .bindNel()
                }
            }
        ) { _, _, _ -> }
    }
}
