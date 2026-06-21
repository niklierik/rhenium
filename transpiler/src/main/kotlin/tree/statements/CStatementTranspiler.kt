package me.eriknikli.rhenium.transpiler.tree.statements

import me.eriknikli.rhenium.ast.tree.statements.Statement
import me.eriknikli.rhenium.ast.tree.statements.vars.VarAssignmentStatement
import me.eriknikli.rhenium.ast.tree.statements.vars.VarDeclarationStatement
import me.eriknikli.rhenium.transpiler.INodeTranspiler
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface IStatementTranspiler : INodeTranspiler<Statement>

@Singleton
class CStatementTranspiler
@Inject
constructor() : IStatementTranspiler {
    @Inject
    lateinit var varDeclarationTranspiler: Lazy<IVarDeclarationTranspiler>

    @Inject
    lateinit var varAssignmentTranspiler: Lazy<IVarAssignmentTranspiler>

    override fun transpile(node: Statement, output: OutputStream) {
        when (node) {
            is VarDeclarationStatement -> varDeclarationTranspiler.value.transpile(node, output)
            is VarAssignmentStatement -> varAssignmentTranspiler.value.transpile(node, output)
            else -> throw IllegalStateException("Unhandled node ${node.javaClass} and cannot transpile it as statement.")
        }
    }
}