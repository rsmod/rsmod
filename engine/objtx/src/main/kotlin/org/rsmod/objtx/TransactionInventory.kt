package org.rsmod.objtx

import java.util.BitSet

public class TransactionInventory<T>(
    public val stackType: StackType,
    public val output: Array<T?>,
    public val image: Array<TransactionObj?>,
    public val placeholders: Boolean = false,
    public val modifiedSlots: BitSet? = null,
) {
    internal val stackAll: Boolean
        get() = stackType == AlwaysStack

    internal val stackNone: Boolean
        get() = stackType == NeverStack

    internal fun freeSpace(): Int = image.count { it == null }

    internal fun occupiedSpace(): Int = image.count { it != null }

    internal fun indexOfNull(startIndex: Int, capacity: Int = image.size): Int? {
        for (i in image.indices) {
            val slot = (i + startIndex) % capacity
            if (image[slot] == null) {
                return slot
            }
        }
        return null
    }

    internal fun indexesOf(
        obj: Int,
        max: Int,
        startIndex: Int = 0,
        slots: MutableList<Int> = emptyIndexList(),
    ): List<Int> {
        for (i in image.indices) {
            val slot = (i + startIndex) % image.size
            if (image[slot]?.id == obj) {
                slots += slot
                if (slots.size >= max) {
                    break
                }
            }
        }
        return slots
    }

    internal fun indexOf(obj: Int, startIndex: Int = 0): Int? {
        for (i in image.indices) {
            val slot = (i + startIndex) % image.size
            if (image[slot]?.id == obj) {
                return slot
            }
        }
        return null
    }

    internal operator fun set(slot: Int, obj: TransactionObj?) {
        image[slot] = obj
        modifiedSlots?.set(slot)
    }

    internal operator fun get(slot: Int): TransactionObj? = image[slot]

    override fun toString(): String =
        "TransactionInventory(stackType=$stackType, invSize=${image.size})"

    public sealed class StackType

    public data object NormalStack : StackType()

    public data object AlwaysStack : StackType()

    public data object NeverStack : StackType()

    private companion object {
        private val reusableIndexList = ArrayList<Int>()

        private fun emptyIndexList(): MutableList<Int> {
            reusableIndexList.clear()
            return reusableIndexList
        }
    }
}
