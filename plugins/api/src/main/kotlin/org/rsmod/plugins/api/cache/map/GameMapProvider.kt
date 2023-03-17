package org.rsmod.plugins.api.cache.map

import org.rsmod.plugins.api.map.GameMap
import javax.inject.Inject
import javax.inject.Provider

public class GameMapProvider @Inject constructor(
    private val loader: GameMapLoader
) : Provider<GameMap> {

    override fun get(): GameMap {
        return loader.load()
    }
}
