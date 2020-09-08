package gg.rsmod.game.cache

import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule
import gg.rsmod.game.config.GameConfig

class CacheModule(private val scope: Scope) : KotlinModule() {

    override fun configure() {
        bind<GameCache>()
            .toProvider<GameCacheProvider>()
            .`in`(scope)
    }
}

private class GameCacheProvider @Inject constructor(
    private val gameConfig: GameConfig
) : Provider<GameCache> {

    override fun get(): GameCache {
        return GameCache.from(gameConfig.cachePath)
    }
}
