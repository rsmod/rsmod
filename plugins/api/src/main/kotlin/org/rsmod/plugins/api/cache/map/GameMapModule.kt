package org.rsmod.plugins.api.cache.map

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.rsmod.plugins.api.map.GameMap

public object GameMapModule : AbstractModule() {

    override fun configure() {
        bind(GameMap::class.java)
            .toProvider(GameMapProvider::class.java)
            .`in`(Scopes.SINGLETON)
    }
}
