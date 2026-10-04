package me.eriknikli.rhenium.semanticAnalyzer.statements

import arrow.core.nel
import arrow.core.raise.either
import arrow.core.raise.ensure
import me.eriknikli.rhenium.ast.tree.statements.BreakStatement
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.LoopControlOutsideLoop
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
    ): Diagnosed<Unit> = either {
        ensure(context.scope.isInsideLoop()) {
            LoopControlOutsideLoop(statement.parserContext, "break").nel()
        }
    }
}
