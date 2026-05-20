package me.eriknikli.rhenium.transpiler.tree.expressions

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.expressions.operators.UnaryOpExpression
import me.eriknikli.rhenium.transpiler.INodeTranspiler
import me.eriknikli.rhenium.transpiler.utils.writeText
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface IUnaryOpTranspiler : INodeTranspiler<UnaryOpExpression>

@Singleton
class CUnaryOpTranspiler
@Inject constructor() : IUnaryOpTranspiler {
    @Inject
    lateinit var expressionTranspilerProvider: Lazy<IExpressionTranspiler>

    private val expressionTranspiler by lazy { expressionTranspilerProvider.get() }

    override fun transpile(node: UnaryOpExpression, output: OutputStream) {
        output.writeText("(")
        val opText = node.operator.cString
        output.writeText(opText)
        expressionTranspiler.transpile(node.expression, output)
        output.writeText(")")
    }
}