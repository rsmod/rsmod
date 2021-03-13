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
    private val types: MutableMap<Int, T> = mutableMapOf()
) : Iterable<T> {

    private var maxId = 0

    val size: Int
        get() = maxId

    fun add(type: T) {
        if (types.containsKey(type.id)) {
            error("Config type at index has already been set (index=${type.id}, type=${type::class.simpleName}).")
        }
        this[type.id] = type
    }

    fun getOrNull(id: Int): T? {
        return types[id]
    }

    operator fun get(id: Int): T = getOrNull(id) ?: error("Null config type (id=$id).")

    operator fun set(id: Int, type: T) {
        maxId = maxId.coerceAtLeast(id)
        types[id] = type
    }

    override fun iterator(): Iterator<T> {
        return types.values.iterator()
    }
}
