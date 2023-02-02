package org.rsmod.game.config

import com.fasterxml.jackson.databind.Module
import com.google.inject.AbstractModule
import com.google.inject.Scopes
import com.google.inject.multibindings.Multibinder
import org.rsmod.game.cache.CachePath
import org.rsmod.game.config.jackson.JacksonConfigModule
import org.rsmod.toml.TomlModule
import java.nio.file.Path

public object GameConfigModule : AbstractModule() {

    override fun configure() {
        install(TomlModule)

        bind(Path::class.java)
            .annotatedWith(DataPath::class.java)
            .toProvider(DataPathProvider::class.java)
            .`in`(Scopes.SINGLETON)

        bind(Path::class.java)
            .annotatedWith(CachePath::class.java)
            .toProvider(CachePathProvider::class.java)
            .`in`(Scopes.SINGLETON)

        bind(GameConfig::class.java)
            .toProvider(GameConfigProvider::class.java)
            .`in`(Scopes.SINGLETON)

        Multibinder.newSetBinder(binder(), Module::class.java)
            .addBinding().to(JacksonConfigModule::class.java)
    }
}
