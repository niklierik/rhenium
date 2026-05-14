package me.eriknikli.rhenium.ast

import me.eriknikli.rhenium.ast.tree.RootNode
import me.eriknikli.rhenium.ast.utils.IParseTreeFactory
import me.eriknikli.rhenium.ast.visitors.IRootVisitor
import org.antlr.v4.runtime.CharStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AstBuilder
@Inject
constructor(
    private val parseTreeFactory: IParseTreeFactory,
    private val rootVisitor: IRootVisitor
) : IAstBuilder {
    override fun parse(stream: CharStream): RootNode {
        val rootContext = parseTreeFactory.parseStream(stream)
        val root = rootVisitor.visitRoot(rootContext)
        return root
    }
}