package org.rsmod.plugins.api.cache

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.openrs2.cache.Cache
import org.openrs2.cache.Store
import org.rsmod.plugins.api.cache.type.CacheTypeListModule

public object CacheModule : AbstractModule() {

    override fun configure() {
        install(CacheTypeListModule)

        bind(Store::class.java)
            .toProvider(StoreProvider::class.java)
            .`in`(Scopes.SINGLETON)

        bind(Cache::class.java)
            .toProvider(CacheProvider::class.java)
            .`in`(Scopes.SINGLETON)
    }
}
