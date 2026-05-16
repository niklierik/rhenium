package me.eriknikli.rhenium.ast.tree.statements.vars

import me.eriknikli.rhenium.ast.tree.expressions.Expression
import me.eriknikli.rhenium.ast.tree.expressions.Identifier
import me.eriknikli.rhenium.ast.tree.statements.Statement
import me.eriknikli.rhenium.semanticContext.tree.statements.VarDeclarationStatementContext

data class VarDeclarationStatement(
    val mutable: Boolean,
    val name: String,
    val expectedType: Identifier?,
    val rightSide: Expression
) : Statement {
    override val context: VarDeclarationStatementContext = VarDeclarationStatementContext()
}