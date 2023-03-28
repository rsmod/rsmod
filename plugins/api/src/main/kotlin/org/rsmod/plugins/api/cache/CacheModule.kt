package org.rsmod.plugins.api.cache

import com.google.inject.AbstractModule
import org.rsmod.plugins.api.cache.build.game.GameCacheModule
import org.rsmod.plugins.api.cache.build.js5.Js5CacheModule
import org.rsmod.plugins.api.cache.name.CacheTypeNameModule
import org.rsmod.plugins.api.cache.type.CacheTypeModule
import org.rsmod.plugins.api.map.GameMapModule

public object CacheModule : AbstractModule() {

    override fun configure() {
        install(CacheTypeModule)
        install(CacheTypeNameModule)
        install(GameCacheModule)
        install(GameMapModule)
        install(Js5CacheModule)
    }
}
