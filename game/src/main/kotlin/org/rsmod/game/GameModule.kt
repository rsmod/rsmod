package org.rsmod.game

import com.google.common.util.concurrent.Service
import com.google.inject.AbstractModule
import com.google.inject.multibindings.Multibinder
import org.rsmod.buffer.BufferModule
import org.rsmod.game.cache.CacheModule
import org.rsmod.game.config.ConfigModule
import org.rsmod.game.coroutine.CoroutineModule

public object GameModule : AbstractModule() {

    override fun configure() {
        install(BufferModule)
        install(CacheModule)
        install(ConfigModule)
        install(CoroutineModule)

        Multibinder.newSetBinder(binder(), Service::class.java)
            .addBinding().to(GameService::class.java)
    }
}
