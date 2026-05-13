package me.eriknikli.rhenium.app

import dagger.Binds
import dagger.Module

@Module
interface AppModule {
    @Binds
    fun bindCompiler(rheniumCompiler: RheniumCompiler): IRheniumCompiler
}