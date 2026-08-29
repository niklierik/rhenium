package me.eriknikli.rhenium.semanticAnalyzer

import arrow.core.raise.either
import arrow.core.raise.mapOrAccumulate
import me.eriknikli.rhenium.ast.tree.RootNode
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.semanticAnalyzer.statements.IStatementNodeDecorator
import me.eriknikli.rhenium.semanticAnalyzer.statements.StatementDecoratorContext
import me.eriknikli.rhenium.semanticContext.scope.globalScope
import javax.inject.Inject
import javax.inject.Singleton

interface IRootNodeDecorator {
    fun decorate(rootNode: RootNode): Diagnosed<Unit>
}

@Singleton
class RootNodeDecorator
@Inject
constructor() : IRootNodeDecorator {
    @Inject
    lateinit var statementDecorator: IStatementNodeDecorator

    override fun decorate(rootNode: RootNode): Diagnosed<Unit> = either {
        val scope = globalScope()

        mapOrAccumulate(rootNode.statements) {
            statementDecorator.decorateStatement(it, StatementDecoratorContext(scope)).bindNel()
        }
    }
}
