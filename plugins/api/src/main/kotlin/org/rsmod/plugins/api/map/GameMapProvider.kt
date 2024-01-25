package org.rsmod.plugins.api.map

import org.openrs2.cache.Cache
import org.rsmod.plugins.api.cache.build.game.GameCache
import org.rsmod.plugins.api.cache.map.GameMapLoader
import org.rsmod.plugins.api.cache.map.xtea.XteaRepository
import org.rsmod.plugins.cache.config.obj.ObjectTypeList
import jakarta.inject.Inject
import jakarta.inject.Provider

public class GameMapProvider @Inject constructor(
    @GameCache private val cache: Cache,
    private val xteas: XteaRepository,
    private val objectTypes: ObjectTypeList
) : Provider<GameMap> {

    override fun get(): GameMap {
        return GameMapLoader.load(cache, xteas, objectTypes)
    }
}
