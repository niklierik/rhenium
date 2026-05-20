package me.eriknikli.rhenium.transpiler.tree.expressions

import me.eriknikli.rhenium.ast.tree.expressions.operators.BinaryOpExpression
import me.eriknikli.rhenium.transpiler.INodeTranspiler
import me.eriknikli.rhenium.transpiler.utils.writeText
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface IBinaryOpTranspiler : INodeTranspiler<BinaryOpExpression>

@Singleton
class CBinaryOpTranspiler
@Inject
constructor() : IBinaryOpTranspiler {
    @Inject
    lateinit var expressionTranspilerProvider: dagger.Lazy<IExpressionTranspiler>

    private val expressionTranspiler by lazy { expressionTranspilerProvider.get() }

    override fun transpile(node: BinaryOpExpression, output: OutputStream) {
        output.writeText("(")
        
        expressionTranspiler.transpile(node.left, output)
        output.writeText(node.operator.cString)
        expressionTranspiler.transpile(node.right, output)

        output.writeText(")")
    }
}