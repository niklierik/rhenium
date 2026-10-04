package me.eriknikli.rhenium.semanticAnalyzer.statements

import arrow.core.nel
import arrow.core.raise.either
import arrow.core.raise.ensure
import me.eriknikli.rhenium.ast.tree.statements.LoopControlStatement
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.LoopControlOutsideLoop
import javax.inject.Inject
import javax.inject.Singleton

interface ILoopControlStatementDecorator {
    fun decorate(statement: LoopControlStatement, context: StatementDecoratorContext): Diagnosed<Unit>
}

@Singleton
class LoopControlStatementDecorator
@Inject
constructor() : ILoopControlStatementDecorator {
    override fun decorate(
        statement: LoopControlStatement,
        context: StatementDecoratorContext
    ): Diagnosed<Unit> = either {
        ensure(context.scope.isInsideLoop()) {
            LoopControlOutsideLoop(statement).nel()
        }
    }
}
