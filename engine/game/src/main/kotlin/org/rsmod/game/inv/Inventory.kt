package org.rsmod.game.inv

import java.util.BitSet
import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.inv.UnpackedInvType
import org.rsmod.game.type.obj.ObjType

public class Inventory(public val type: UnpackedInvType, public val objs: Array<InvObj?>) :
    Iterable<InvObj?> {
    public val size: Int
        get() = objs.size

    public val modifiedSlots: BitSet = BitSet()

    public fun isNotEmpty(): Boolean = !isEmpty()

    public fun isEmpty(): Boolean = objs.all { it == null }

    public fun isFull(): Boolean = objs.all { it != null }

    public fun freeSpace(): Int = objs.count { it == null }

    public fun occupiedSpace(): Int = objs.count { it != null }

    public fun hasFreeSpace(): Boolean = objs.any { it == null }

    public fun mapNotNullSlotObjs(): List<Pair<Int, InvObj>> = objs.mapNotNullEntries()

    public fun fillNulls() {
        for (i in objs.indices) {
            if (objs[i] == null) {
                continue
            }
            objs[i] = null
            modifiedSlots.set(i)
        }
    }

    public fun getValue(slot: Int): InvObj =
        this[slot] ?: throw NoSuchElementException("Slot $slot is missing in the inv.")

    public fun hasModifiedSlots(): Boolean = !modifiedSlots.isEmpty

    public fun clearModifiedSlots() {
        modifiedSlots.clear()
    }

    public operator fun get(slot: Int): InvObj? = objs.getOrNull(slot)

    public operator fun set(slot: Int, obj: InvObj?) {
        objs[slot] = obj
        modifiedSlots.set(slot)
    }

    public fun count(type: ObjType): Int = objs.count { it != null && it.isAssociatedWith(type) }

    public infix operator fun contains(type: ObjType): Boolean =
        objs.any { it != null && it.isAssociatedWith(type) }

    override fun iterator(): Iterator<InvObj?> = objs.iterator()

    override fun toString(): String = "Inventory(type=$type, objs=${mapNotNullSlotObjs()})"

    public companion object {
        @Suppress("DEPRECATION")
        public fun create(type: UnpackedInvType): Inventory {
            val objs = arrayOfNulls<InvObj>(type.size)
            if (type.stock != null) {
                for (i in type.stock.indices) {
                    val copy = type.stock[i] ?: continue
                    objs[i] = InvObj(copy.obj, copy.count)
                }
            }
            return Inventory(type, objs)
        }

        private fun Array<InvObj?>.mapNotNullEntries(): List<Pair<Int, InvObj>> =
            mapIndexedNotNull { slot, obj ->
                obj?.let { slot to obj }
            }
    }
}
