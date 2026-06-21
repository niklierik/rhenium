package me.eriknikli.rhenium.transpiler.tree.expressions

import me.eriknikli.rhenium.ast.tree.expressions.Identifier
import me.eriknikli.rhenium.ast.tree.expressions.LeftValue
import me.eriknikli.rhenium.transpiler.INodeTranspiler
import me.eriknikli.rhenium.transpiler.utils.writeText
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface ILeftValueTranspiler : INodeTranspiler<LeftValue>

@Singleton
class CLeftValueTranspiler
@Inject
constructor() : ILeftValueTranspiler {
    override fun transpile(node: LeftValue, output: OutputStream) {
        return when (node) {
            is Identifier -> {
                val symbol = node.context.symbol
                val cName = symbol.cName
                output.writeText(cName)
            }

            else -> throw IllegalStateException("Illegal left value cannot be transpiled: $node")
        }
    }
}