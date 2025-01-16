package org.rsmod.objtx

class Inventory(
    val stackType: TransactionInventory.StackType,
    val objs: Array<Obj?>,
    val placeholders: Boolean = false,
) {
    val indices: IntRange
        get() = objs.indices

    val size: Int
        get() = objs.size

    fun freeSpace(): Int = objs.count { it == null }

    fun occupiedSpace(): Int = objs.count { it != null }

    fun isEmpty(): Boolean = occupiedSpace() == 0

    operator fun get(slot: Int): Obj? = objs.getOrNull(slot)

    operator fun set(slot: Int, obj: Obj?): Unit = objs.set(slot, obj)
}
