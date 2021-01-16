package org.rsmod.game.cache

import com.github.michaelbull.logging.InlineLogger

private val logger = InlineLogger()

interface ConfigType {

    val id: Int
}

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

open class ConfigTypeList<T : ConfigType>(
    private val types: MutableList<T> = mutableListOf()
) : List<T> by types {

    fun add(type: T) {
        if (types.size > type.id) {
            error("Config type at index has already been set (index=${type.id}, type=${type::class.simpleName}).")
        }
        types.add(type)
    }

    operator fun set(index: Int, type: T) {
        check(types.size >= index) {
            "Config type with index does not exist (index=$index, type=${type::class.simpleName})."
        }
        types[index] = type
    }
}
