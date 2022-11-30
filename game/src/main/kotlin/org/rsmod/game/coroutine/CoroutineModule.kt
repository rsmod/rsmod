package org.rsmod.game.coroutine

import com.google.inject.AbstractModule
import com.google.inject.Scope
import com.google.inject.name.Names
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

public class CoroutineModule(private val scope: Scope) : AbstractModule() {

    override fun configure() {
        bind(CoroutineDispatcher::class.java)
            .annotatedWith(Names.named("ioCoroutineDispatcher"))
            .toInstance(Dispatchers.IO)
        bind(CoroutineDispatcher::class.java)
            .annotatedWith(Names.named("gameCoroutineDispatcher"))
            .toProvider(GameCoroutineDispatcherProvider::class.java)
            .`in`(scope)

        bind(IoCoroutineScope::class.java).`in`(scope)
        bind(GameCoroutineScope::class.java).`in`(scope)
    }
}
