package me.eriknikli.rhenium.app

import dagger.Component
import me.eriknikli.rhenium.ast.AstModule
import javax.inject.Singleton

@Component(
    modules = [AstModule::class, AppModule::class]
)
@Singleton
interface RheniumCompilerFactory {
    fun makeCompiler(): IRheniumCompiler
}