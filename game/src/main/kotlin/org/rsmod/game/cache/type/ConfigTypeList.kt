package org.rsmod.game.cache.type

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

    fun containsKey(key: Int): Boolean {
        return types.containsKey(key)
    }

    fun containsValue(value: T): Boolean {
        return types.containsValue(value)
    }

    override fun iterator(): Iterator<T> {
        return types.values.iterator()
    }
}
