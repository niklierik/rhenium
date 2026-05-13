package me.eriknikli.rhenium.ast

import dagger.Binds
import dagger.Module
import me.eriknikli.rhenium.ast.utils.IParseTreeFactory
import me.eriknikli.rhenium.ast.utils.ParseTreeFactory

@Module
interface AstModule {
    @Binds
    fun bindAstBuilder(builder: AstBuilder): IAstBuilder

    @Binds
    fun bindParseTreeFactory(parseTreeFactory: ParseTreeFactory): IParseTreeFactory
}