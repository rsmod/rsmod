package org.rsmod.plugins.api.cache.game

import io.netty.buffer.ByteBufAllocator
import org.openrs2.cache.Cache
import org.openrs2.cache.Store
import org.rsmod.game.config.GameConfig
import org.rsmod.plugins.api.gameCachePath
import java.io.FileNotFoundException
import javax.inject.Inject
import javax.inject.Provider

public class GameCacheProvider @Inject constructor(
    @GameCache private val store: Store,
    private val alloc: ByteBufAllocator,
    config: GameConfig
) : Provider<Cache> {

    private val path = config.gameCachePath

    private val fnfInstructions = """
        Place cache files in ${path.toAbsolutePath()}
        and `xteas.json` in ${path.parent.toAbsolutePath()}
    """.trimIndent()

    override fun get(): Cache {
        return try {
            Cache.open(store, alloc)
        } catch (fnf: FileNotFoundException) {
            throw FileNotFoundException(
                "Cache not found.\n$fnfInstructions"
            )
        } catch (t: Throwable) {
            throw t
        }
    }
}
