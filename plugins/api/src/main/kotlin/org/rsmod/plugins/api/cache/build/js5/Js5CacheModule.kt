package org.rsmod.plugins.api.cache.build.js5

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.openrs2.cache.Cache
import org.openrs2.cache.Js5MasterIndex
import org.openrs2.cache.Store

public object Js5CacheModule : AbstractModule() {

    override fun configure() {
        bind(Js5MasterIndex::class.java)
            .toProvider(Js5MasterIndexProvider::class.java)
            .`in`(Scopes.SINGLETON)

        bind(Store::class.java)
            .annotatedWith(Js5Cache::class.java)
            .toProvider(Js5StoreProvider::class.java)
            .`in`(Scopes.SINGLETON)

        bind(Cache::class.java)
            .annotatedWith(Js5Cache::class.java)
            .toProvider(Js5CacheProvider::class.java)
            .`in`(Scopes.SINGLETON)
    }
}
