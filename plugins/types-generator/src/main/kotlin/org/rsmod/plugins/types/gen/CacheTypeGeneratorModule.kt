package org.rsmod.plugins.types.gen

import com.google.inject.AbstractModule
import org.rsmod.buffer.BufferModule
import org.rsmod.game.cache.CacheModule
import org.rsmod.game.config.GameConfigModule
import org.rsmod.toml.TomlModule

public object CacheTypeGeneratorModule : AbstractModule() {

    override fun configure() {
        install(BufferModule)
        install(CacheModule)
        install(GameConfigModule)
        install(TomlModule)
    }
}
