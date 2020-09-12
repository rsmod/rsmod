package gg.rsmod.game.cache

import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule
import gg.rsmod.game.config.GameConfig
import io.guthix.js5.Js5Cache
import io.guthix.js5.container.disk.Js5DiskStore
import io.guthix.js5.container.heap.Js5HeapStore
import java.nio.file.Files

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
        val path = gameConfig.cachePath
        if (!Files.isDirectory(path)) {
            error("Cache directory does not exist: ${path.toAbsolutePath()}")
        }
        val diskStore = Js5DiskStore.open(path)
        val heapStore = Js5HeapStore.open(diskStore)
        val cache = Js5Cache(heapStore)
        return GameCache(heapStore, cache)
    }
}
