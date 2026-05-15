package me.eriknikli.rhenium.transpiler.tree.statements

import me.eriknikli.rhenium.ast.tree.statements.vars.VarDeclarationStatement
import me.eriknikli.rhenium.transpiler.INodeTranspiler
import me.eriknikli.rhenium.transpiler.tree.expressions.IExpressionTranspiler
import me.eriknikli.rhenium.transpiler.utils.writeText
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface IVarDeclarationTranspiler : INodeTranspiler<VarDeclarationStatement>

@Singleton
class CVarDeclarationTranspiler
@Inject
constructor() : IVarDeclarationTranspiler {
    @Inject
    lateinit var expressionTranspiler: IExpressionTranspiler

    override fun transpile(node: VarDeclarationStatement, output: OutputStream) {
        val cType = node.context.typeToDeclare.cTypeName

        output.writeText(cType)
        output.writeText(" ")
        output.writeText(node.name)
        output.writeText("=")
        expressionTranspiler.transpile(node.rightSide, output)
        output.writeText(";")
    }
}