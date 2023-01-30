package org.rsmod.plugins.types.gen

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.rsmod.buffer.BufferModule
import org.rsmod.game.cache.CacheModule
import org.rsmod.game.cache.CachePath
import org.rsmod.game.types.NamedTypeModule
import java.nio.file.Path

object CacheTypeGeneratorModule : AbstractModule() {

    override fun configure() {
        install(BufferModule)
        install(CacheModule)
        install(NamedTypeModule)

        bind(Path::class.java)
            .annotatedWith(CachePath::class.java)
            .toProvider(CachePathProvider::class.java)
            .`in`(Scopes.SINGLETON)
    }
}
