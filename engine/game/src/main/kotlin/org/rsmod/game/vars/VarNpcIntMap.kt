package org.rsmod.game.vars

import it.unimi.dsi.fastutil.ints.Int2IntMap
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import org.rsmod.game.type.varbit.VarBitType
import org.rsmod.game.type.varp.VarpType
import org.rsmod.utils.bits.getBits
import org.rsmod.utils.bits.withBits

@JvmInline
public value class VarNpcIntMap(public val backing: Int2IntMap = Int2IntOpenHashMap()) {
    public fun remove(key: VarpType) {
        backing.remove(key.id)
    }

    public operator fun get(key: VarpType): Int = backing.getOrDefault(key.id, 0)

    public operator fun set(key: VarpType, value: Int?) {
        if (value == null) {
            backing.remove(key.id)
        } else {
            backing[key.id] = value
        }
    }

    public operator fun get(varp: VarBitType): Int {
        val mappedValue = this[varp.baseVar]
        val extracted = mappedValue.getBits(varp.bits)
        return extracted
    }

    public operator fun set(varp: VarBitType, value: Int) {
        val mappedValue = this[varp.baseVar]
        val packedValue = mappedValue.withBits(varp.bits, value)
        set(varp.baseVar, packedValue)
    }

    public operator fun contains(key: VarpType): Boolean = backing.containsKey(key.id)

    override fun toString(): String = backing.toString()
}
