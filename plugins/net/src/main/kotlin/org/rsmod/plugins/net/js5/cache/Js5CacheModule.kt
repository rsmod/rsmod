package org.rsmod.plugins.net.js5.cache

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.openrs2.cache.Js5MasterIndex
import org.openrs2.cache.Store
import org.rsmod.plugins.api.cache.type.CacheTypeListModule

public object Js5CacheModule : AbstractModule() {

    override fun configure() {
        install(CacheTypeListModule)

        bind(Js5MasterIndex::class.java)
            .toProvider(Js5MasterIndexProvider::class.java)
            .`in`(Scopes.SINGLETON)

        bind(Store::class.java)
            .annotatedWith(Js5Store::class.java)
            .toProvider(Js5StoreProvider::class.java)
            .`in`(Scopes.SINGLETON)
    }
}
