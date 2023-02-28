package org.rsmod.game

import com.google.common.util.concurrent.Service
import com.google.inject.AbstractModule
import com.google.inject.Scopes
import com.google.inject.multibindings.Multibinder
import org.rsmod.game.client.ClientModule
import org.rsmod.game.config.GameConfigModule
import org.rsmod.game.dispatcher.CoroutineDispatcherModule
import org.rsmod.game.events.GameEventBus
import org.rsmod.game.job.GameJobModule
import org.rsmod.game.model.mob.list.PlayerList

public object GameModule : AbstractModule() {

    override fun configure() {
        install(ClientModule)
        install(CoroutineDispatcherModule)
        install(GameConfigModule)
        install(GameJobModule)

        bind(GameEventBus::class.java).`in`(Scopes.SINGLETON)
        bind(PlayerList::class.java).`in`(Scopes.SINGLETON)

        Multibinder.newSetBinder(binder(), Service::class.java)
            .addBinding().to(GameService::class.java)
    }
}
