package org.rsmod.game.entity

public sealed class EntityList<T>(public val capacity: Int, public val slotPadding: Int) :
    Iterable<T> {
    private val entries: MutableList<T?> = MutableList(capacity) { null }
    private var lastUsedSlot = 0

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

    public fun getValue(slot: Int): T = entries[slot] ?: throw IndexOutOfBoundsException()

    public operator fun get(slot: Int): T? = entries.getOrNull(slot)

    public operator fun set(slot: Int, entity: T) {
        assertSlot(slot)
        lastUsedSlot = slot
        entries[slot] = entity
    }

    override fun iterator(): Iterator<T> {
        return EntityListIterator()
    }

    private fun assertSlot(slot: Int) {
        if (slot in entries.indices) {
            return
        }
        throw IndexOutOfBoundsException(
            "Slot out of bounds (slot=$slot, valid=${entries.indices})."
        )
    }

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
