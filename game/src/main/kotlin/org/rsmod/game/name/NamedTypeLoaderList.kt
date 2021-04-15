package org.rsmod.game.name

import com.github.michaelbull.logging.InlineLogger

private val logger = InlineLogger()

class NamedTypeLoaderList(
    private val loaders: MutableList<NamedTypeLoader> = mutableListOf()
) : List<NamedTypeLoader> by loaders {

    fun register(loader: NamedTypeLoader) {
        loaders.add(loader)
        logger.debug { "Register named type loader (loader=$loader)" }
    }

    operator fun NamedTypeLoader.unaryMinus() {
        register(this)
    }
}
