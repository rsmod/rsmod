package org.rsmod.api.type.updater

import jakarta.inject.Inject
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.copyToRecursively
import kotlin.io.path.deleteRecursively
import kotlin.io.path.exists
import org.openrs2.cache.Cache
import org.rsmod.annotations.EnrichedCache
import org.rsmod.annotations.GameCache
import org.rsmod.annotations.Js5Cache
import org.rsmod.annotations.VanillaCache

public class TypeUpdaterCacheSync
@Inject
constructor(
    @EnrichedCache private val enrichedCachePath: Path,
    @VanillaCache private val vanillaCachePath: Path,
    @GameCache private val gameCachePath: Path,
    @Js5Cache private val js5CachePath: Path,
    // Required to inject enriched cache so that it may create a default path when applicable.
    @EnrichedCache private val enrichedCache: Cache,
) {
    /**
     * Replaces the game and js5 caches by syncing them from their base sources:
     * - `gameCachePath` is overwritten by the contents of `enrichedCachePath`
     * - `js5CachePath` is overwritten by the contents of `vanillaCachePath`
     *
     * Existing destination directories are deleted before copying.
     */
    public fun syncFromBaseCaches() {
        deleteExistingCache(gameCachePath)
        copyCache(enrichedCachePath, gameCachePath)

        deleteExistingCache(js5CachePath)
        copyCache(vanillaCachePath, js5CachePath)
    }

    @OptIn(ExperimentalPathApi::class)
    private fun deleteExistingCache(cachePath: Path) {
        cachePath.deleteRecursively()
        check(!cachePath.exists())
    }

    @OptIn(ExperimentalPathApi::class)
    private fun copyCache(from: Path, dest: Path) {
        from.copyToRecursively(target = dest, followLinks = true, overwrite = false)
    }
}
