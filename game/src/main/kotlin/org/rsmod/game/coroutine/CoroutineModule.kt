package org.rsmod.game.coroutine

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

public object CoroutineModule : AbstractModule() {

    override fun configure() {
        bind(CoroutineDispatcher::class.java)
            .annotatedWith(Names.named("ioCoroutineDispatcher"))
            .toInstance(Dispatchers.IO)
        bind(CoroutineDispatcher::class.java)
            .annotatedWith(Names.named("gameCoroutineDispatcher"))
            .toProvider(GameCoroutineDispatcherProvider::class.java)

        bind(IoCoroutineScope::class.java)
        bind(GameCoroutineScope::class.java)
    }
}
