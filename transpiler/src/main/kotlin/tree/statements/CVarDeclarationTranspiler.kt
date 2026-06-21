package me.eriknikli.rhenium.transpiler.tree.statements

import dagger.Lazy
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
    lateinit var expressionTranspiler: Lazy<IExpressionTranspiler>

    override fun transpile(node: VarDeclarationStatement, output: OutputStream) {
        val context = node.context
        val cType = context.typeToDeclare.cName
        val symbol = context.symbolInfo

        output.writeText(cType)
        output.writeText(" ")
        output.writeText(symbol.cName)
        output.writeText("=")
        expressionTranspiler.get().transpile(node.rightSide, output)
        output.writeText(";")
    }
}