package me.eriknikli.rhenium.semanticAnalyzer.statements

import arrow.core.raise.either
import arrow.core.raise.zipOrAccumulate
import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.statements.ForStatement
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import javax.inject.Inject
import javax.inject.Singleton

interface IForStatementDecorator {
    fun decorate(statement: ForStatement, context: StatementDecoratorContext): Diagnosed<Unit>
}

@Singleton
class ForStatementDecorator
@Inject
constructor() : IForStatementDecorator {
    @Inject
    lateinit var conditionDecoratorProvider: Lazy<IConditionDecorator>

    @Inject
    lateinit var statementNodeDecoratorProvider: Lazy<IStatementNodeDecorator>

    private val conditionDecorator by lazy { conditionDecoratorProvider.get() }
    private val statementNodeDecorator by lazy { statementNodeDecoratorProvider.get() }

    override fun decorate(
        statement: ForStatement,
        context: StatementDecoratorContext
    ): Diagnosed<Unit> = either {
        val loopContext = StatementDecoratorContext(context.scope.createChild(isLoop = true))

        zipOrAccumulate(
            { statementNodeDecorator.decorateStatement(statement.initClause, loopContext).bindNel() },
            { conditionDecorator.decorate(statement.condition, loopContext).bindNel() },
            { statementNodeDecorator.decorateStatement(statement.iterationExpression, loopContext).bindNel() },
            { statementNodeDecorator.decorateStatement(statement.body, loopContext).bindNel() }
        ) { _, _, _, _ -> }
    }
}
