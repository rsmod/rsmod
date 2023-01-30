package org.rsmod.game.config

import java.nio.file.Path
import javax.inject.Inject
import javax.inject.Provider

public class CachePathProvider @Inject constructor(
    private val config: GameConfig
) : Provider<Path> {

    override fun get(): Path = config.packedCachePath
}
