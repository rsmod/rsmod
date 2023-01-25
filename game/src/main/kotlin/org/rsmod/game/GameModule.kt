package org.rsmod.game

import com.google.inject.AbstractModule
import org.rsmod.buffer.BufferModule
import org.rsmod.game.cache.CacheModule
import org.rsmod.game.config.ConfigModule
import org.rsmod.game.coroutine.CoroutineModule
import org.rsmod.game.net.NetworkModule
import org.rsmod.game.service.ServiceModule

public object GameModule : AbstractModule() {

    override fun configure() {
        install(BufferModule)
        install(CacheModule)
        install(ConfigModule)
        install(CoroutineModule)
        install(NetworkModule)
        install(ServiceModule)
    }
}
