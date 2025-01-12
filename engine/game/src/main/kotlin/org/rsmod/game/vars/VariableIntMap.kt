package org.rsmod.game.vars

import it.unimi.dsi.fastutil.ints.Int2IntMap
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import org.rsmod.game.type.varbit.VarBitType
import org.rsmod.game.type.varp.VarpType
import org.rsmod.utils.bits.getBits

@JvmInline
public value class VariableIntMap(public val backing: Int2IntMap = Int2IntOpenHashMap()) {
    public fun remove(key: VarpType) {
        backing.remove(key.id)
    }

    public operator fun get(key: VarpType): Int? = backing.getOrDefault(key.id, null)

    public operator fun set(key: VarpType, value: Int?) {
        if (value == null) {
            backing.remove(key.id)
        } else {
            backing[key.id] = value
        }
    }

    public operator fun get(varp: VarBitType): Int? {
        val mappedValue = this[varp.baseVar] ?: return null
        val extracted = mappedValue.getBits(varp.bits)
        return extracted
    }

    public operator fun contains(key: VarpType): Boolean = backing.containsKey(key.id)

    override fun toString(): String = backing.toString()
}
