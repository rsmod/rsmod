package org.rsmod.game.coroutine

import com.google.inject.AbstractModule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.rsmod.game.coroutine.main.GameCoroutineDispatcher
import org.rsmod.game.coroutine.main.GameCoroutineDispatcherProvider
import org.rsmod.game.coroutine.main.GameCoroutineScope
import org.rsmod.game.coroutine.io.IOCoroutineDispatcher
import org.rsmod.game.coroutine.io.IOCoroutineScope

public object CoroutineModule : AbstractModule() {

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
