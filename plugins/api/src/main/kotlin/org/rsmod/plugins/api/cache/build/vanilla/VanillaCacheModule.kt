package org.rsmod.plugins.api.cache.build.vanilla

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.openrs2.cache.Cache
import org.openrs2.cache.Store

public object VanillaCacheModule : AbstractModule() {

    override fun configure() {
        bind(Store::class.java)
            .annotatedWith(VanillaCache::class.java)
            .toProvider(VanillaStoreProvider::class.java)
            .`in`(Scopes.SINGLETON)

        bind(Cache::class.java)
            .annotatedWith(VanillaCache::class.java)
            .toProvider(VanillaCacheProvider::class.java)
            .`in`(Scopes.SINGLETON)
    }
}
