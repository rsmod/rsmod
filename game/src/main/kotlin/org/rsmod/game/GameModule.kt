package org.rsmod.game

import com.google.common.util.concurrent.Service
import com.google.inject.AbstractModule
import com.google.inject.multibindings.Multibinder
import org.rsmod.game.client.ClientModule
import org.rsmod.game.config.GameConfigModule
import org.rsmod.game.dispatcher.CoroutineDispatcherModule
import org.rsmod.game.job.GameJobModule

public object GameModule : AbstractModule() {

    override fun configure() {
        install(ClientModule)
        install(CoroutineDispatcherModule)
        install(GameConfigModule)
        install(GameJobModule)
        Multibinder.newSetBinder(binder(), Service::class.java)
            .addBinding().to(GameService::class.java)
    }
}
