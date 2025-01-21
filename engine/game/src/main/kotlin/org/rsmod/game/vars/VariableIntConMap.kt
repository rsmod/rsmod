package org.rsmod.game.vars

import it.unimi.dsi.fastutil.ints.Int2IntMap
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import org.rsmod.game.type.varcon.VarConType
import org.rsmod.game.type.varconbit.VarConBitType
import org.rsmod.utils.bits.getBits

@JvmInline
public value class VariableIntConMap(public val backing: Int2IntMap = Int2IntOpenHashMap()) {
    public fun remove(key: VarConType) {
        backing.remove(key.id)
    }

    public operator fun get(key: VarConType): Int = backing.getOrDefault(key.id, 0)

    public operator fun set(key: VarConType, value: Int?) {
        if (value == null) {
            backing.remove(key.id)
        } else {
            backing[key.id] = value
        }
    }

    public operator fun get(varp: VarConBitType): Int {
        val mappedValue = this[varp.baseVar]
        val extracted = mappedValue.getBits(varp.bits)
        return extracted
    }

    public operator fun contains(key: VarConType): Boolean = backing.containsKey(key.id)

    override fun toString(): String = backing.toString()
}
