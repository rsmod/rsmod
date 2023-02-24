package org.rsmod.plugins.api.cache.name

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.rsmod.plugins.api.cache.build.game.GameCache
import org.rsmod.plugins.types.NamedTypeMapHolder

public object CacheTypeNameModule : AbstractModule() {

    override fun configure() {
        bind(CacheTypeNameLoader::class.java)
            .`in`(Scopes.SINGLETON)

        bind(NamedTypeMapHolder::class.java)
            .annotatedWith(GameCache::class.java)
            .toProvider(CacheTypeNamesProvider::class.java)
            .`in`(Scopes.SINGLETON)
    }
}
