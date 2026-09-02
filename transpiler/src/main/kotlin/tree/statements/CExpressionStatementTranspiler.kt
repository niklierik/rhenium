package me.eriknikli.rhenium.transpiler.tree.statements

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.statements.ExpressionStatement
import me.eriknikli.rhenium.transpiler.INodeTranspiler
import me.eriknikli.rhenium.transpiler.tree.expressions.IExpressionTranspiler
import me.eriknikli.rhenium.transpiler.utils.writeText
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface IExpressionStatementTranspiler : INodeTranspiler<ExpressionStatement>

@Singleton
class CExpressionStatementTranspiler
@Inject
constructor() : IExpressionStatementTranspiler {
    @Inject
    lateinit var expressionTranspiler: Lazy<IExpressionTranspiler>

    override fun transpile(node: ExpressionStatement, output: OutputStream) {
        expressionTranspiler.get().transpile(node.expression, output)
        output.writeText(";")
    }
}
