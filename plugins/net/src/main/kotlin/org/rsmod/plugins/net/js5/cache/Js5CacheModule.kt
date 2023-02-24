package org.rsmod.plugins.net.js5.cache

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.openrs2.cache.Js5MasterIndex
import org.openrs2.cache.Store

public object Js5CacheModule : AbstractModule() {

    override fun configure() {
        bind(Js5MasterIndex::class.java)
            .toProvider(Js5MasterIndexProvider::class.java)
            .`in`(Scopes.SINGLETON)

        bind(Store::class.java)
            .annotatedWith(Js5Store::class.java)
            .toProvider(Js5StoreProvider::class.java)
            .`in`(Scopes.SINGLETON)
    }
}
