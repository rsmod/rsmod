package org.rsmod.game.model.vars

class VarMap(private val vars: MutableMap<VarKey<*>, Any> = mutableMapOf()) {

    operator fun <T> get(key: VarKey<T>): T? {
        return getOrNull(key)
    }

    operator fun <T> set(key: VarKey<T>, value: T?) {
        val map = mapAs<T>()
        if (value != null) {
            map[key] = value as Any
        } else {
            map.remove(key)
        }
    }

    fun <T> getValue(key: VarKey<T>): T {
        return getOrNull(key) ?: error("Key $key is missing in the map.")
    }

    fun <T> containsKey(key: VarKey<T>): Boolean {
        return vars.containsKey(key)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getOrNull(key: VarKey<T>): T? {
        val map = mapAs<T>()
        return map[key] as? T
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> mapAs(): MutableMap<VarKey<T>, Any> {
        return vars as MutableMap<VarKey<T>, Any>
    }
}
