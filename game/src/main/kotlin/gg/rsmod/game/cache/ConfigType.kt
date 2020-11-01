package gg.rsmod.game.cache

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import kotlin.reflect.KClass

private val logger = InlineLogger()

interface ConfigType {

    val id: Int
}

interface ConfigTypeLoader {

    fun load()

    fun save()
}

class ConfigTypeLoaderMap(
    private val loaders: MutableMap<KClass<out ConfigType>, ConfigTypeLoader> = mutableMapOf()
) : Map<KClass<out ConfigType>, ConfigTypeLoader> by loaders {

    fun <T : ConfigType> register(type: KClass<T>, loader: ConfigTypeLoader) {
        if (loaders.containsKey(type)) {
            error("Config type loader has already set registered (type=${type::simpleName})")
        }
        logger.debug { "Register config type loader (type=${type::simpleName})" }
        loaders[type] = loader
    }
}

open class ConfigTypeList<T : ConfigType>(
    private val types: MutableList<T>
) : List<T> by types {

    @Inject
    constructor() : this(mutableListOf())

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
