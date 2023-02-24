package org.rsmod.plugins.api.cache.js5

import io.netty.buffer.ByteBufAllocator
import org.openrs2.cache.Cache
import org.openrs2.cache.Store
import org.rsmod.game.config.GameConfig
import org.rsmod.plugins.api.js5CachePath
import java.io.FileNotFoundException
import javax.inject.Inject
import javax.inject.Provider

public class Js5CacheProvider @Inject constructor(
    @Js5Cache private val store: Store,
    private val alloc: ByteBufAllocator,
    config: GameConfig
) : Provider<Cache> {

    private val path = config.js5CachePath

    private val fnfInstructions = """
        Place cache files in ${path.toAbsolutePath()}
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
