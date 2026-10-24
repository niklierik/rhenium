package me.eriknikli.rhenium.ast.tree

import org.antlr.v4.runtime.ParserRuleContext

data class RootNode(
    override val parserContext: ParserRuleContext,
    val declarations: List<Declaration>,
) : AstNode {
    val entries: List<EntryDeclaration>
        get() = declarations.filterIsInstance<EntryDeclaration>()

    val functions: List<FunctionDeclaration>
        get() = declarations.filterIsInstance<FunctionDeclaration>()
}
