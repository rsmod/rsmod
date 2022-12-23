package org.rsmod.game

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.openrs2.cache.Cache
import org.openrs2.cache.Js5MasterIndex
import org.openrs2.cache.Store
import org.rsmod.buffer.BufferModule
import org.rsmod.config.ConfigModule
import org.rsmod.game.cache.CacheProvider
import org.rsmod.game.cache.Js5MasterIndexProvider
import org.rsmod.game.cache.StoreProvider
import org.rsmod.game.coroutine.CoroutineModule
import org.rsmod.game.net.NetworkModule
import org.rsmod.game.service.ServiceModule

public object GameModule : AbstractModule() {

    override fun configure() {
        install(BufferModule)
        install(ConfigModule)
        install(CoroutineModule)
        install(NetworkModule)
        install(ServiceModule)

        bind(Store::class.java)
            .toProvider(StoreProvider::class.java)
            .`in`(Scopes.SINGLETON)

        bind(Js5MasterIndex::class.java)
            .toProvider(Js5MasterIndexProvider::class.java)
            .`in`(Scopes.SINGLETON)

        bind(Cache::class.java)
            .toProvider(CacheProvider::class.java)
            .`in`(Scopes.SINGLETON)
    }
}
