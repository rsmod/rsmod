package org.rsmod.game.cache

import com.google.inject.Provider
import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule
import io.guthix.js5.Js5Cache
import io.guthix.js5.container.disk.Js5DiskStore
import org.rsmod.game.config.GameConfig
import java.nio.file.Files
import javax.inject.Inject

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
        val path = gameConfig.cachePath.resolve(PACKED_FOLDER)
        if (!Files.isDirectory(path)) {
            error("Cache directory does not exist: ${path.toAbsolutePath()}")
        }
        val diskStore = Js5DiskStore.open(path)
        val cache = Js5Cache(diskStore)
        return GameCache(path, diskStore, cache)
    }

    companion object {
        private const val PACKED_FOLDER = "packed"
    }
}
