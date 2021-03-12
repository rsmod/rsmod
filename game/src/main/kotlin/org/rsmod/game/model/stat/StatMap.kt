package org.rsmod.game.model.stat

class StatMap(
    private val skills: MutableMap<StatKey, Stat> = mutableMapOf()
) : MutableMap<StatKey, Stat> by skills {

    /**
     * Creates a deep-copy of this [StatMap].
     */
    fun copy(): StatMap {
        val map = skills.entries.associate { (k, v) -> k to v.copy() } as MutableMap
        return StatMap(map)
    }
}
