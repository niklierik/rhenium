package me.eriknikli.rhenium.app

import dagger.Binds
import dagger.Module
import dagger.Provides
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Module
interface AppModule {
    @Binds
    fun bindCompiler(rheniumCompiler: RheniumCompiler): IRheniumCompiler

    companion object {
        @Provides
        fun provideLogger(): Logger = LoggerFactory.getLogger("Rhenium")
    }
}