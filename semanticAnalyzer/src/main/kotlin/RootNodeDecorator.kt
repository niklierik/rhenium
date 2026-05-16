package me.eriknikli.rhenium.semanticAnalyzer

import me.eriknikli.rhenium.ast.tree.RootNode
import me.eriknikli.rhenium.semanticAnalyzer.statements.IStatementNodeDecorator
import me.eriknikli.rhenium.semanticAnalyzer.statements.StatementDecoratorContext
import me.eriknikli.rhenium.semanticContext.scope.globalScope
import javax.inject.Inject
import javax.inject.Singleton

interface IRootNodeDecorator {
    fun decorate(rootNode: RootNode)
}

@Singleton
class RootNodeDecorator
@Inject
constructor() : IRootNodeDecorator {
    @Inject
    lateinit var statementDecorator: IStatementNodeDecorator

    override fun decorate(rootNode: RootNode) {
        val scope = globalScope()

        for (statement in rootNode.statements) {
            statementDecorator.decorateStatement(statement, StatementDecoratorContext(scope))
        }
    }

}