package org.rsmod.module

import com.google.inject.AbstractModule
import com.google.inject.Provider
import com.google.inject.Scopes

public abstract class ExtendedModule : AbstractModule() {
    override fun configure() {
        bind()
    }

    public abstract fun bind()

    protected inline fun <reified T> bindInstance() {
        bind(T::class.java).`in`(Scopes.SINGLETON)
    }

    protected inline fun <reified T> bindSingleton(type: T) {
        bind(T::class.java).toInstance(type)
    }

    protected inline fun <reified T> bindProvider(provider: Class<out Provider<T>>) {
        bind(T::class.java).toProvider(provider).`in`(Scopes.SINGLETON)
    }

    protected inline fun <reified P, reified C : P> bindBaseInstance() {
        bind(P::class.java).to(C::class.java).`in`(Scopes.SINGLETON)
    }
}
