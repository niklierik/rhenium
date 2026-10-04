package me.eriknikli.rhenium.semanticAnalyzer.statements

import arrow.core.raise.either
import arrow.core.raise.zipOrAccumulate
import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.statements.WhileStatement
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import javax.inject.Inject
import javax.inject.Singleton

interface IWhileStatementDecorator {
    fun decorate(statement: WhileStatement, context: StatementDecoratorContext): Diagnosed<Unit>
}

@Singleton
class WhileStatementDecorator
@Inject
constructor() : IWhileStatementDecorator {
    @Inject
    lateinit var conditionDecoratorProvider: Lazy<IConditionDecorator>

    @Inject
    lateinit var statementNodeDecoratorProvider: Lazy<IStatementNodeDecorator>

    private val conditionDecorator by lazy { conditionDecoratorProvider.get() }
    private val statementNodeDecorator by lazy { statementNodeDecoratorProvider.get() }

    override fun decorate(
        statement: WhileStatement,
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
                    .decorateStatement(
                        statement.body,
                        StatementDecoratorContext(context.scope.createChild(isLoop = true))
                    )
                    .bindNel()
            }
        ) { _, _ -> }
    }
}
