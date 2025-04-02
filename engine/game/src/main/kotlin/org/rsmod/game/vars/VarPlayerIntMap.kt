package org.rsmod.game.vars

import it.unimi.dsi.fastutil.ints.Int2IntMap
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import org.rsmod.game.type.varbit.VarBitType
import org.rsmod.game.type.varp.VarpType
import org.rsmod.utils.bits.bitMask
import org.rsmod.utils.bits.getBits

/**
 * A map for storing integer-based player variables (varps and varbits).
 *
 * This class **does not provide `set` operators** in order to prevent direct modifications. Player
 * varps must be kept in sync with the client immediately when changed.
 *
 * Since the networking layer should **not** be mixed with the model layer, this map relies on
 * separate systems to handle synchronization. These systems should ensure that any changes here are
 * properly transmitted to the client as needed.
 */
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

    public companion object {
        public fun assertVarBitBounds(varp: VarBitType, value: Int) {
            val maxValue = varp.maxValue()
            require(value in 0..maxValue) {
                "Varbit overflow on varbit ${varp.internalId} " +
                    "Value $value is outside the range 0-$maxValue (type=$varp)"
            }
        }

        private fun VarBitType.maxValue(): Long = bits.bitMask
    }
}
