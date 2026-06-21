package me.eriknikli.rhenium.transpiler.tree.statements

import me.eriknikli.rhenium.ast.tree.statements.vars.VarAssignmentStatement
import me.eriknikli.rhenium.transpiler.INodeTranspiler
import me.eriknikli.rhenium.transpiler.tree.expressions.IExpressionTranspiler
import me.eriknikli.rhenium.transpiler.tree.expressions.ILeftValueTranspiler
import me.eriknikli.rhenium.transpiler.utils.writeText
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface IVarAssignmentTranspiler : INodeTranspiler<VarAssignmentStatement>

@Singleton
class CVarAssignmentTranspiler
@Inject
constructor() : IVarAssignmentTranspiler {
    @Inject
    lateinit var expressionTranspiler: Lazy<IExpressionTranspiler>

    override fun transpile(node: VarAssignmentStatement, output: OutputStream) {
        val context = node.context

        leftValueTranspiler.value.transpile(node., output)
        output.writeText("=(")
        expressionTranspiler.value.transpile(node.rightValue, output)
        output.writeText(");")
    }
}