package org.rsmod.plugins.api.map

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import org.rsmod.plugins.api.map.collision.CollisionFlagMapProvider

public object GameMapModule : AbstractModule() {

    override fun configure() {
        bind(GameMap::class.java)
            .toProvider(GameMapProvider::class.java)
            .`in`(Scopes.SINGLETON)

        bind(CollisionFlagMap::class.java)
            .toProvider(CollisionFlagMapProvider::class.java)
            .`in`(Scopes.SINGLETON)
    }
}
