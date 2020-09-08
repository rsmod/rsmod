package gg.rsmod.game.cache

import net.runelite.cache.fs.Store
import java.nio.file.Files
import java.nio.file.Path

class GameCache(val store: Store) {

    val archiveCount: Int
        get() = store.indexes.size

    fun init() {
        store.load()
    }

    companion object {

        fun from(path: Path): GameCache {
            require(Files.isDirectory(path)) { "Invalid cache directory: ${path.toAbsolutePath()}" }
            val store = Store(path.toFile())
            return GameCache(store)
        }
    }
}
