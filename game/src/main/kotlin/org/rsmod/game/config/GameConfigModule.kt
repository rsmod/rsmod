package org.rsmod.game.config

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.rsmod.game.jackson.JacksonGameModule
import org.rsmod.toml.TomlModule

public object GameConfigModule : AbstractModule() {

    override fun configure() {
        install(JacksonGameModule)
        install(TomlModule)

        bind(GameConfig::class.java)
            .toProvider(GameConfigProvider::class.java)
            .`in`(Scopes.SINGLETON)
    }
}
