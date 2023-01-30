package org.rsmod.plugins.types.gen

import java.nio.file.Path
import javax.inject.Provider

private val CACHE_PATH = Path.of(".data/cache/packed")

internal class CachePathProvider : Provider<Path> {

    override fun get(): Path = CACHE_PATH
}
