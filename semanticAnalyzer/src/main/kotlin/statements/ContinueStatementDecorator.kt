package me.eriknikli.rhenium.semanticAnalyzer.statements

import arrow.core.right
import me.eriknikli.rhenium.ast.tree.statements.ContinueStatement
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import javax.inject.Inject
import javax.inject.Singleton

interface IContinueStatementDecorator {
    fun decorate(statement: ContinueStatement, context: StatementDecoratorContext): Diagnosed<Unit>
}

@Singleton
class ContinueStatementDecorator
@Inject
constructor() : IContinueStatementDecorator {
    override fun decorate(
        statement: ContinueStatement,
        context: StatementDecoratorContext
    ): Diagnosed<Unit> = Unit.right()
}
