package me.eriknikli.rhenium.semanticAnalyzer

import me.eriknikli.rhenium.ast.tree.RootNode
import javax.inject.Inject
import javax.inject.Singleton

interface ISemanticAnalyzer {
    fun decorateSemanticContext(root: RootNode)
}

@Singleton
class SemanticAnalyzer
@Inject
constructor(
    private val rootNodeDecorator: IRootNodeDecorator
) : ISemanticAnalyzer {
    override fun decorateSemanticContext(root: RootNode) {
        rootNodeDecorator.decorate(root)
    }
}