package me.eriknikli.rhenium.transpiler.tree.expressions

import me.eriknikli.rhenium.ast.tree.expressions.literals.*
import me.eriknikli.rhenium.transpiler.INodeTranspiler
import me.eriknikli.rhenium.transpiler.utils.writeText
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface ILiteralExpressionTranspiler : INodeTranspiler<Literal<*>>

@Singleton
class CLiteralExpressionTranspiler
@Inject
constructor() : ILiteralExpressionTranspiler {
    override fun transpile(literal: Literal<*>, output: OutputStream) {
        when (literal) {
            is U64Literal -> output.writeText("${literal.value}llu")
            is U32Literal -> output.writeText("${literal.value}lu")
            is U16Literal -> output.writeText("${literal.value}")
            is U8Literal -> output.writeText("${literal.value}")

            is I64Literal -> output.writeText("${literal.value}ll")
            is I32Literal -> output.writeText("${literal.value}l")
            is I16Literal -> output.writeText("${literal.value}")
            is I8Literal -> output.writeText("${literal.value}")

            is F64Literal -> output.writeText("${literal.value}")
            is F32Literal -> output.writeText("${literal.value}f")
        }
    }
}