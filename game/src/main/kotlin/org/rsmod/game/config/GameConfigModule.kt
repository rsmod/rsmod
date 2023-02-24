package org.rsmod.game.config

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.rsmod.game.jackson.JacksonGameModule
import org.rsmod.toml.TomlModule
import java.nio.file.Path

public object GameConfigModule : AbstractModule() {

    override fun configure() {
        install(JacksonGameModule)
        install(TomlModule)

        bind(Path::class.java)
            .annotatedWith(DataPath::class.java)
            .toProvider(DataPathProvider::class.java)
            .`in`(Scopes.SINGLETON)

        bind(GameConfig::class.java)
            .toProvider(GameConfigProvider::class.java)
            .`in`(Scopes.SINGLETON)
    }
}
