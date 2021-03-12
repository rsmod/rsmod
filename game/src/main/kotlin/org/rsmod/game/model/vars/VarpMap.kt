package org.rsmod.game.model.vars

class VarpMap(
    private val varps: MutableMap<Int, Int> = mutableMapOf()
) : Map<Int, Int> by varps {

    override fun get(key: Int): Int? {
        return varps[key]
    }

    operator fun set(key: Int, value: Int): Int? {
        if (value == 0) {
            return varps.remove(key)
        }
        return varps.put(key, value)
    }

    fun forEach(action: (Map.Entry<Int, Int>) -> Unit) {
        varps.forEach(action)
    }

    /**
     * Creates a deep-copy of this [VarpMap].
     */
    fun copy(): VarpMap {
        val map = varps.entries.associate { (k, v) -> k to v } as MutableMap
        return VarpMap(map)
    }
}
