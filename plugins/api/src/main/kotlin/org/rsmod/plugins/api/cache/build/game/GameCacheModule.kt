package org.rsmod.plugins.api.cache.build.game

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.openrs2.cache.Cache
import org.openrs2.cache.Store

public object GameCacheModule : AbstractModule() {

    override fun configure() {
        bind(Store::class.java)
            .annotatedWith(GameCache::class.java)
            .toProvider(GameStoreProvider::class.java)
            .`in`(Scopes.SINGLETON)

        bind(Cache::class.java)
            .annotatedWith(GameCache::class.java)
            .toProvider(GameCacheProvider::class.java)
            .`in`(Scopes.SINGLETON)
    }
}
