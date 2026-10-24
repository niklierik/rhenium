package me.eriknikli.rhenium.ast.tree

import me.eriknikli.rhenium.ast.tree.expressions.Identifier
import me.eriknikli.rhenium.ast.tree.statements.BlockStatement
import me.eriknikli.rhenium.semanticContext.tree.declarations.FunctionDeclarationContext
import org.antlr.v4.runtime.ParserRuleContext

data class FunctionDeclaration(
    override val parserContext: ParserRuleContext,
    val name: String,
    val parameters: List<Parameter>,
    val returnType: Identifier?,
    val body: BlockStatement
) : Declaration {
    val context: FunctionDeclarationContext = FunctionDeclarationContext()
}
