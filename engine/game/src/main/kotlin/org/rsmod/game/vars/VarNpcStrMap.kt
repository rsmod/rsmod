package org.rsmod.game.vars

import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.rsmod.game.type.varn.VarnType

@JvmInline
public value class VarNpcStrMap(
    public val backing: Int2ObjectMap<String> = Int2ObjectOpenHashMap()
) {
    public fun remove(key: VarnType) {
        backing.remove(key.id)
    }

    public operator fun get(key: VarnType): String? = backing.getOrDefault(key.id, null)

    public operator fun set(key: VarnType, value: String?) {
        if (value == null) {
            backing.remove(key.id)
        } else {
            backing[key.id] = value
        }
    }

    public operator fun contains(key: VarnType): Boolean = backing.containsKey(key.id)

    override fun toString(): String = backing.toString()
}
