package me.eriknikli.rhenium.transpiler.tree.expressions

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.expressions.Expression
import me.eriknikli.rhenium.ast.tree.expressions.literals.Literal
import me.eriknikli.rhenium.ast.tree.expressions.operators.BinaryOpExpression
import me.eriknikli.rhenium.ast.tree.expressions.operators.UnaryOpExpression
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
    lateinit var literalExpressionTranspilerProvider: Lazy<ILiteralExpressionTranspiler>

    private val literalExpressionTranspiler by lazy { literalExpressionTranspilerProvider.get() }

    @Inject
    lateinit var binaryOpExpressionTranspilerProvider: Lazy<IBinaryOpTranspiler>

    private val binaryOpExpressionTranspiler by lazy { binaryOpExpressionTranspilerProvider.get() }


    @Inject
    lateinit var unaryOpTranspilerProvider: Lazy<IUnaryOpTranspiler>

    private val unaryOpTranspiler by lazy { unaryOpTranspilerProvider.get() }


    override fun transpile(node: Expression, output: OutputStream) {
        when (node) {
            is Literal<*> -> literalExpressionTranspiler.transpile(node, output)
            is UnaryOpExpression -> unaryOpTranspiler.transpile(node, output)
            is BinaryOpExpression -> binaryOpExpressionTranspiler.transpile(node, output)
            else -> throw IllegalStateException("Unhandled node ${node.javaClass} and cannot transpile it as expression.")
        }
    }
}