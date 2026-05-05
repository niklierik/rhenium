package me.eriknikli.lang.common.di

import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Singleton

@Singleton
class DI(val injector: Injector) {
    inline fun <reified T> inject(): T {
        return injector.getInstance<T>(T::class.java)
    }
}

fun createInjector(): Injector {
    val module = CommonModule()
    val injector = Guice.createInjector()
    module.injector = injector
    return injector
}