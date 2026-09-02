package me.eriknikli.rhenium.transpiler.tree.statements

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.statements.PrintStatement
import me.eriknikli.rhenium.semanticContext.scope.types.BooleanType
import me.eriknikli.rhenium.transpiler.INodeTranspiler
import me.eriknikli.rhenium.transpiler.tree.expressions.IExpressionTranspiler
import me.eriknikli.rhenium.transpiler.utils.writeText
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface IPrintTranspiler : INodeTranspiler<PrintStatement>

@Singleton
class CPrintTranspiler
@Inject
constructor() : IPrintTranspiler {
    @Inject
    lateinit var expressionTranspiler: Lazy<IExpressionTranspiler>

    override fun transpile(node: PrintStatement, output: OutputStream) {
        val expression = node.expression

        if (expression == null) {
            output.writeText("""printf("\n");""")
            return
        }

        val type = expression.context.type
        val format = type.cFormat
            ?: throw IllegalStateException("$type has no printf format and cannot be printed.")

        output.writeText("printf(")
        output.writeText(format)
        if (node.newLine) {
            output.writeText(" \"\\n\"")
        }
        output.writeText(",")

        if (type is BooleanType) {
            output.writeText("(")
            expressionTranspiler.get().transpile(expression, output)
            output.writeText(")?\"true\":\"false\"")
        } else {
            output.writeText("(${type.cName})(")
            expressionTranspiler.get().transpile(expression, output)
            output.writeText(")")
        }

        output.writeText(");")
    }
}
