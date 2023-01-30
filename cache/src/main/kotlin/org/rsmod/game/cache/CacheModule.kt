package org.rsmod.game.cache

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.openrs2.cache.Cache
import org.openrs2.cache.Js5MasterIndex
import org.openrs2.cache.Store

public object CacheModule : AbstractModule() {

    override fun configure() {
        bind(Js5MasterIndex::class.java)
            .toProvider(Js5MasterIndexProvider::class.java)
            .`in`(Scopes.SINGLETON)

        bind(Store::class.java)
            .toProvider(StoreProvider::class.java)
            .`in`(Scopes.SINGLETON)

        bind(Cache::class.java)
            .toProvider(CacheProvider::class.java)
            .`in`(Scopes.SINGLETON)
    }
}
