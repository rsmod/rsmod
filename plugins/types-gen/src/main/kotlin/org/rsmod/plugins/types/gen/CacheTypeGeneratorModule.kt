package org.rsmod.plugins.types.gen

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.openrs2.cache.Store
import org.rsmod.buffer.BufferModule
import org.rsmod.game.cache.CacheModule
import org.rsmod.game.types.NamedTypeModule

object CacheTypeGeneratorModule : AbstractModule() {

    override fun configure() {
        install(BufferModule)
        install(CacheModule)
        install(NamedTypeModule)

        bind(Store::class.java)
            .toProvider(StoreProvider::class.java)
            .`in`(Scopes.SINGLETON)
    }
}
