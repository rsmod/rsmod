package org.rsmod.game.name

import com.github.michaelbull.logging.InlineLogger

private val logger = InlineLogger()

interface NamedTypeLoader {

    fun load()
}

class NamedTypeLoaderList(
    private val loaders: MutableList<NamedTypeLoader> = mutableListOf()
) : List<NamedTypeLoader> by loaders {

    fun register(loader: NamedTypeLoader) {
        logger.debug { "Register named type loader (type=${loader.javaClass.simpleName})" }
        loaders.add(loader)
    }
}
