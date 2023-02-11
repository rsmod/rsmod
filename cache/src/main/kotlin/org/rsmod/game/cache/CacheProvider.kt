package org.rsmod.game.cache

import io.netty.buffer.ByteBufAllocator
import org.openrs2.cache.Cache
import org.openrs2.cache.Store
import java.io.FileNotFoundException
import java.nio.file.Path
import javax.inject.Inject
import javax.inject.Provider

public class CacheProvider @Inject constructor(
    private val store: Store,
    private val alloc: ByteBufAllocator,
    @CachePath private val path: Path
) : Provider<Cache> {

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
