package me.eriknikli.lang.common.di

import com.google.inject.AbstractModule
import com.google.inject.Injector
import tools.jackson.databind.ObjectMapper

class CommonModule : AbstractModule() {
    lateinit var injector: Injector

    override fun configure() {
        val objectMapper = ObjectMapper()
        bind(ObjectMapper::class.java).toInstance(objectMapper)
        bind(DI::class.java).toProvider {
            DI(injector)
        }
    }
}