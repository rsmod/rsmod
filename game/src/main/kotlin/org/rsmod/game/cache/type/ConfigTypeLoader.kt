package org.rsmod.game.cache.type

import com.github.michaelbull.logging.InlineLogger

private val logger = InlineLogger()

interface ConfigTypeLoader {

    fun load()
}

class ConfigTypeLoaderList(
    private val loaders: MutableList<ConfigTypeLoader> = mutableListOf()
) : List<ConfigTypeLoader> by loaders {

    fun register(loader: ConfigTypeLoader) {
        logger.debug { "Register config type loader (type=${loader.javaClass.simpleName})" }
        loaders.add(loader)
    }
}
