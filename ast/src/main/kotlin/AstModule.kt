package me.eriknikli.rhenium.ast

import dagger.Binds
import dagger.Module
import me.eriknikli.rhenium.ast.utils.IParseTreeFactory
import me.eriknikli.rhenium.ast.utils.ParseTreeFactory
import me.eriknikli.rhenium.ast.visitors.IRootVisitor
import me.eriknikli.rhenium.ast.visitors.RootVisitor
import me.eriknikli.rhenium.ast.visitors.expressions.literals.ILiteralTypeVisitor
import me.eriknikli.rhenium.ast.visitors.expressions.literals.ILiteralVisitor
import me.eriknikli.rhenium.ast.visitors.expressions.literals.LiteralTypeVisitor
import me.eriknikli.rhenium.ast.visitors.expressions.literals.LiteralVisitor

@Module
interface AstModule {
    @Binds
    fun bindAstBuilder(builder: AstBuilder): IAstBuilder

    @Binds
    fun bindParseTreeFactory(parseTreeFactory: ParseTreeFactory): IParseTreeFactory

    @Binds
    fun bindLiteralTypeVisitor(literalTypeVisitor: LiteralTypeVisitor): ILiteralTypeVisitor

    @Binds
    fun bindRootVisitor(rootVisitor: RootVisitor): IRootVisitor

    @Binds
    fun bindLiteralVisitor(literalVisitor: LiteralVisitor): ILiteralVisitor
}