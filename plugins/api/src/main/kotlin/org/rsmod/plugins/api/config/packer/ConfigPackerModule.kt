package org.rsmod.plugins.api.config.packer

import com.google.inject.AbstractModule
import org.rsmod.buffer.BufferModule
import org.rsmod.game.config.GameConfigModule
import org.rsmod.plugins.api.cache.build.game.GameCacheModule
import org.rsmod.plugins.api.cache.build.js5.Js5CacheModule
import org.rsmod.plugins.api.cache.name.CacheTypeNameModule
import org.rsmod.toml.TomlModule

public object ConfigPackerModule : AbstractModule() {

    override fun configure() {
        install(BufferModule)
        install(CacheTypeNameModule)
        install(GameCacheModule)
        install(GameConfigModule)
        install(Js5CacheModule)
        install(TomlModule)
    }
}
