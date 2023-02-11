package org.rsmod.game.dispatcher

import com.google.inject.AbstractModule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.rsmod.game.dispatcher.io.IOCoroutineDispatcher
import org.rsmod.game.dispatcher.io.IOCoroutineScope
import org.rsmod.game.dispatcher.main.MainCoroutineDispatcher
import org.rsmod.game.dispatcher.main.MainCoroutineDispatcherProvider
import org.rsmod.game.dispatcher.main.MainCoroutineScope

public object CoroutineDispatcherModule : AbstractModule() {

    override fun configure() {
        bind(CoroutineDispatcher::class.java)
            .annotatedWith(IOCoroutineDispatcher::class.java)
            .toInstance(Dispatchers.IO)
        bind(CoroutineDispatcher::class.java)
            .annotatedWith(MainCoroutineDispatcher::class.java)
            .toProvider(MainCoroutineDispatcherProvider::class.java)

        bind(IOCoroutineScope::class.java)
        bind(MainCoroutineScope::class.java)
    }
}
