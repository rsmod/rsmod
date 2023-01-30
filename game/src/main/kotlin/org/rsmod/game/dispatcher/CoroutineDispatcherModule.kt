package org.rsmod.game.dispatcher

import com.google.inject.AbstractModule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.rsmod.game.dispatcher.io.IOCoroutineDispatcher
import org.rsmod.game.dispatcher.io.IOCoroutineScope
import org.rsmod.game.dispatcher.main.GameCoroutineDispatcher
import org.rsmod.game.dispatcher.main.GameCoroutineDispatcherProvider
import org.rsmod.game.dispatcher.main.GameCoroutineScope

public object CoroutineDispatcherModule : AbstractModule() {

    override fun configure() {
        bind(CoroutineDispatcher::class.java)
            .annotatedWith(IOCoroutineDispatcher::class.java)
            .toInstance(Dispatchers.IO)
        bind(CoroutineDispatcher::class.java)
            .annotatedWith(GameCoroutineDispatcher::class.java)
            .toProvider(GameCoroutineDispatcherProvider::class.java)

        bind(IOCoroutineScope::class.java)
        bind(GameCoroutineScope::class.java)
    }
}
