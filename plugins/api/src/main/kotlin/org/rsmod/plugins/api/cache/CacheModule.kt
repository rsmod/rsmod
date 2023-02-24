package org.rsmod.plugins.api.cache

import com.google.inject.AbstractModule
import org.rsmod.plugins.api.cache.game.GameCacheModule
import org.rsmod.plugins.api.cache.js5.Js5CacheModule
import org.rsmod.plugins.api.cache.type.CacheTypeModule

public object CacheModule : AbstractModule() {

    override fun configure() {
        install(CacheTypeModule)
        install(GameCacheModule)
        install(Js5CacheModule)
    }
}
