package me.eriknikli.rhenium.transpiler.tree

import me.eriknikli.rhenium.ast.tree.RootNode
import me.eriknikli.rhenium.transpiler.INodeTranspiler
import me.eriknikli.rhenium.transpiler.tree.statements.IStatementTranspiler
import me.eriknikli.rhenium.transpiler.utils.writeText
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface IRootTranspiler : INodeTranspiler<RootNode>

@Singleton
class CRootTranspiler
@Inject
constructor() : IRootTranspiler {
    @Inject
    lateinit var statementTranspiler: IStatementTranspiler

    override fun transpile(node: RootNode, output: OutputStream) {
        output.writeText("int main(){")
        for (statement in node.statements) {
            statementTranspiler.transpile(statement, output)
        }
        output.writeText(";return 0;}")
    }
}