package me.eriknikli.rhenium.semanticAnalyzer.statements

import arrow.core.right
import me.eriknikli.rhenium.ast.tree.statements.BreakStatement
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import javax.inject.Inject
import javax.inject.Singleton

interface IBreakStatementDecorator {
    fun decorate(statement: BreakStatement, context: StatementDecoratorContext): Diagnosed<Unit>
}

@Singleton
class BreakStatementDecorator
@Inject
constructor() : IBreakStatementDecorator {
    override fun decorate(
        statement: BreakStatement,
        context: StatementDecoratorContext
    ): Diagnosed<Unit> = Unit.right()
}
