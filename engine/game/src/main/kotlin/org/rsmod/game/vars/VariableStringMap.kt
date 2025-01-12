package org.rsmod.game.vars

import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.rsmod.game.type.varp.VarpType

@JvmInline
public value class VariableStringMap(
    public val backing: Int2ObjectMap<String> = Int2ObjectOpenHashMap()
) {
    public fun remove(key: VarpType) {
        backing.remove(key.id)
    }

    public operator fun get(key: VarpType): String? = backing.getOrDefault(key.id, null)

    public operator fun set(key: VarpType, value: String?) {
        if (value == null) {
            backing.remove(key.id)
        } else {
            backing[key.id] = value
        }
    }

    public operator fun contains(key: VarpType): Boolean = backing.containsKey(key.id)

    override fun toString(): String = backing.toString()
}
