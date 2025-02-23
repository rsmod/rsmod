package org.rsmod.module

import com.google.inject.AbstractModule
import com.google.inject.Provider
import com.google.inject.Scopes
import com.google.inject.multibindings.Multibinder

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

    protected inline fun <reified P> bindBaseInstance(impl: Class<out P>) {
        bindBaseInstance(P::class.java, impl)
    }

    @PublishedApi
    internal fun <P, C : P> bindBaseInstance(base: Class<P>, impl: Class<C>) {
        bind(base).to(impl).`in`(Scopes.SINGLETON)
    }

    protected inline fun <reified P> addSetBinding(impl: Class<out P>) {
        addSetBinding(P::class.java, impl)
    }

    @PublishedApi
    internal fun <P, C : P> addSetBinding(base: Class<P>, impl: Class<C>) {
        Multibinder.newSetBinder(binder(), base).addBinding().to(impl).`in`(Scopes.SINGLETON)
    }
}
