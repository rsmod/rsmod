package org.rsmod.game.entity

public abstract class EntityList<T>(
    public val capacity: Int,
    public val slotPadding: Int = 0,
    initialLastSlotUsed: Int = 0,
) : Iterable<T> {
    private val entries: MutableList<T?> = MutableList(capacity) { null }
    private var lastUsedSlot = initialLastSlotUsed

    public fun nextFreeSlot(): Int? {
        val startSlot = lastUsedSlot + 1

        for (slot in startSlot until capacity) {
            if (this[slot] == null) {
                return slot
            }
        }

        for (slot in slotPadding until startSlot) {
            if (this[slot] == null) {
                return slot
            }
        }

        return null
    }

    public fun remove(slot: Int) {
        assertSlot(slot)
        entries[slot] = null
    }

    public operator fun set(slot: Int, entity: T) {
        assertSlot(slot)
        lastUsedSlot = slot
        entries[slot] = entity
    }

    public fun getValue(slot: Int): T =
        entries[slot] ?: throw NoSuchElementException("Slot $slot is missing in the list.")

    public operator fun get(slot: Int): T? = entries.getOrNull(slot)

    private fun assertSlot(slot: Int) {
        if (slot in entries.indices) {
            return
        }
        throw IndexOutOfBoundsException(
            "Slot out of bounds (slot=$slot, valid=${entries.indices})."
        )
    }

    override fun iterator(): Iterator<T> = EntityListIterator()

    private inner class EntityListIterator : Iterator<T> {
        private var cursor = 0

        override fun hasNext(): Boolean {
            while (cursor < entries.size) {
                val entry = entries[cursor]
                if (entry != null) {
                    return true
                }
                cursor++
            }
            return false
        }

        override fun next(): T {
            val entry = entries[cursor++]
            return checkNotNull(entry)
        }
    }
}
