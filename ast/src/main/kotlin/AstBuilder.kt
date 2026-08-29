package me.eriknikli.rhenium.ast

import arrow.core.flatMap
import me.eriknikli.rhenium.ast.tree.RootNode
import me.eriknikli.rhenium.ast.utils.IParseTreeFactory
import me.eriknikli.rhenium.ast.visitors.IRootVisitor
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
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
    override fun parse(stream: CharStream): Diagnosed<RootNode> {
        return parseTreeFactory.parseStream(stream).flatMap { rootVisitor.visitRoot(it) }
    }
}
