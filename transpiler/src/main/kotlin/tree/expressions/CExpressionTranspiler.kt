package me.eriknikli.rhenium.transpiler.tree.expressions

import me.eriknikli.rhenium.ast.tree.expressions.Expression
import me.eriknikli.rhenium.ast.tree.expressions.literals.Literal
import me.eriknikli.rhenium.transpiler.INodeTranspiler
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface IExpressionTranspiler : INodeTranspiler<Expression>

@Singleton
class CExpressionTranspiler
@Inject
constructor() : IExpressionTranspiler {
    @Inject
    lateinit var literalExpressionTranspiler: ILiteralExpressionTranspiler

    override fun transpile(node: Expression, output: OutputStream) {
        when (node) {
            is Literal<*> -> literalExpressionTranspiler.transpile(node, output)
            else -> throw IllegalStateException("Unhandled node ${node.javaClass} and cannot transpile it as expression.")
        }
    }
}