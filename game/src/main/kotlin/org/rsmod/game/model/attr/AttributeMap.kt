package org.rsmod.game.model.attr

class AttributeMap(private val vars: MutableMap<AttributeKey<*>, Any> = mutableMapOf()) {

    operator fun <T> get(key: AttributeKey<T>): T? {
        return getOrNull(key)
    }

    operator fun <T> set(key: AttributeKey<T>, value: T?) {
        if (value != null) {
            vars[key] = value
        } else {
            vars.remove(key)
        }
    }

    fun <T> getValue(key: AttributeKey<T>): T {
        return getOrNull(key) ?: error("Key $key is missing in the map.")
    }

    fun <T> containsKey(key: AttributeKey<T>): Boolean {
        return vars.containsKey(key)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getOrNull(key: AttributeKey<T>): T? {
        val map = vars as MutableMap<AttributeKey<T>, Any>
        return map[key] as? T
    }
}
