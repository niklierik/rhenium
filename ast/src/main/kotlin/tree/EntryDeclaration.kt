package me.eriknikli.rhenium.ast.tree

import me.eriknikli.rhenium.ast.tree.statements.BlockStatement
import me.eriknikli.rhenium.semanticContext.tree.declarations.EntryDeclarationContext
import org.antlr.v4.runtime.ParserRuleContext

data class EntryDeclaration(
    override val parserContext: ParserRuleContext,
    override val name: String,
    override val body: BlockStatement
) : Declaration {
    val context: EntryDeclarationContext = EntryDeclarationContext()
}
