package org.rsmod.game.vars

import it.unimi.dsi.fastutil.ints.Int2IntMap
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import org.rsmod.game.type.varbit.VarBitType
import org.rsmod.game.type.varp.VarpType
import org.rsmod.utils.bits.getBits

@JvmInline
public value class VarPlayerIntMap(public val backing: Int2IntMap = Int2IntOpenHashMap()) {
    public operator fun get(key: VarpType): Int = backing.getOrDefault(key.id, 0)

    public operator fun get(varp: VarBitType): Int {
        val mappedValue = this[varp.baseVar]
        val extracted = mappedValue.getBits(varp.bits)
        return extracted
    }

    public operator fun contains(key: VarpType): Boolean = backing.containsKey(key.id)

    override fun toString(): String = backing.toString()
}
