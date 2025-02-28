package org.rsmod.game.vars

import it.unimi.dsi.fastutil.ints.Int2IntMap
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import org.rsmod.game.type.varn.VarnType
import org.rsmod.game.type.varnbit.VarnBitType
import org.rsmod.utils.bits.getBits
import org.rsmod.utils.bits.withBits

@JvmInline
public value class VarNpcIntMap(public val backing: Int2IntMap = Int2IntOpenHashMap()) {
    public fun remove(key: VarnType) {
        backing.remove(key.id)
    }

    public operator fun get(key: VarnType): Int = backing.getOrDefault(key.id, 0)

    public operator fun set(key: VarnType, value: Int?) {
        if (value == null) {
            backing.remove(key.id)
        } else {
            backing[key.id] = value
        }
    }

    public operator fun get(varp: VarnBitType): Int {
        val mappedValue = this[varp.baseVar]
        val extracted = mappedValue.getBits(varp.bits)
        return extracted
    }

    public operator fun set(varp: VarnBitType, value: Int) {
        val mappedValue = this[varp.baseVar]
        val packedValue = mappedValue.withBits(varp.bits, value)
        set(varp.baseVar, packedValue)
    }

    public operator fun contains(key: VarnType): Boolean = backing.containsKey(key.id)

    override fun toString(): String = backing.toString()
}
