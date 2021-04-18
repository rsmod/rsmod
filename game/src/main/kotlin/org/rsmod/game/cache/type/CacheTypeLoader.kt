package org.rsmod.game.cache.type

import com.github.michaelbull.logging.InlineLogger

private val logger = InlineLogger()

interface CacheTypeLoader {

    fun load()
}

class CacheTypeLoaderList(
    private val loaders: MutableList<CacheTypeLoader> = mutableListOf()
) : List<CacheTypeLoader> by loaders {

    fun register(loader: CacheTypeLoader) {
        logger.debug { "Register cache type loader (type=${loader.javaClass.simpleName})" }
        loaders.add(loader)
    }
}
