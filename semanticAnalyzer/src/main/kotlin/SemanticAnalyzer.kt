package me.eriknikli.rhenium.semanticAnalyzer

import me.eriknikli.rhenium.ast.tree.RootNode
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import javax.inject.Inject
import javax.inject.Singleton

interface ISemanticAnalyzer {
    fun decorateSemanticContext(root: RootNode): Diagnosed<Unit>
}

@Singleton
class SemanticAnalyzer
@Inject
constructor(
    private val rootNodeDecorator: IRootNodeDecorator
) : ISemanticAnalyzer {
    override fun decorateSemanticContext(root: RootNode): Diagnosed<Unit> {
        return rootNodeDecorator.decorate(root)
    }
}
