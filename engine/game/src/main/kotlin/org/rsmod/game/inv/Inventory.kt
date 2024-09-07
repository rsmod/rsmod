package org.rsmod.game.inv

import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.inv.UnpackedInvType
import org.rsmod.game.type.obj.ObjType

public class Inventory(public val type: UnpackedInvType, public val objs: Array<InvObj?>) :
    Iterable<InvObj?> {
    public val size: Int
        get() = objs.size

    public fun isNotEmpty(): Boolean = !isEmpty()

    public fun isEmpty(): Boolean = objs.all { it == null }

    public fun isFull(): Boolean = objs.all { it != null }

    public fun freeSpace(): Int = objs.count { it == null }

    public fun occupiedSpace(): Int = objs.count { it != null }

    public fun hasFreeSpace(): Boolean = objs.any { it == null }

    public fun mapNotNullSlotObjs(): List<Pair<Int, InvObj>> = objs.mapNotNullEntries()

    public fun fillNulls() {
        objs.fill(null)
    }

    public fun getValue(slot: Int): InvObj =
        this[slot] ?: throw NoSuchElementException("Slot $slot is missing in the inv.")

    public operator fun get(slot: Int): InvObj? = objs.getOrNull(slot)

    public operator fun set(slot: Int, obj: InvObj?) {
        objs[slot] = obj
    }

    public operator fun set(slot: Int, type: ObjType) {
        objs[slot] = InvObj(type, count = 1)
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
