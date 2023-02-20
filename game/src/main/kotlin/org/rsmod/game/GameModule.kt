package org.rsmod.game

import com.google.common.util.concurrent.Service
import com.google.inject.AbstractModule
import com.google.inject.Scopes
import com.google.inject.multibindings.Multibinder
import org.rsmod.buffer.BufferModule
import org.rsmod.game.cache.CacheModule
import org.rsmod.game.client.ClientModule
import org.rsmod.game.config.GameConfigModule
import org.rsmod.game.coroutine.GameCoroutineModule
import org.rsmod.game.dispatcher.CoroutineDispatcherModule
import org.rsmod.game.events.GameEventBus
import org.rsmod.game.model.mob.list.PlayerList

public object GameModule : AbstractModule() {

    override fun configure() {
        install(BufferModule)
        install(CacheModule)
        install(ClientModule)
        install(CoroutineDispatcherModule)
        install(GameConfigModule)
        install(GameCoroutineModule)

        bind(GameEventBus::class.java).`in`(Scopes.SINGLETON)
        bind(PlayerList::class.java).`in`(Scopes.SINGLETON)

        Multibinder.newSetBinder(binder(), Service::class.java)
            .addBinding().to(GameService::class.java)
    }
}
