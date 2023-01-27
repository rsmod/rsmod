package org.rsmod.game.model.mob.list

import org.rsmod.game.model.mob.Mob

public sealed class MobList<T : Mob>(
    public val capacity: Int,
    private val indexPadding: Int,
    private val mobs: MutableList<T?> = MutableList(capacity) { null }
) : List<T?> by mobs {

    private var lastUsedIndex = 0

    override fun isEmpty(): Boolean {
        return mobs.all { it == null }
    }

    public fun isFull(): Boolean {
        return nextAvailableIndex() == null
    }

    public fun nextAvailableIndex(): Int? {
        for (i in lastUsedIndex + 1 until capacity) {
            if (this[i] == null) return i
        }
        for (i in indexPadding until lastUsedIndex) {
            if (this[i] == null) return i
        }
        return null
    }

    /**
     * Sets the element at the given [index] to the given [mob].
     * This method can be called using the index operator.
     *
     * If [mob] is a non-null value then [lastUsedIndex] is assigned as [index].
     *
     * If the [index] is out of bounds of this array, throws an [IndexOutOfBoundsException].
     */
    public operator fun set(index: Int, mob: T?) {
        if (index !in indices) {
            throw IndexOutOfBoundsException("Index out of bounds (index=$index, valid=$indices).")
        }
        if (mob != null) lastUsedIndex = index
        mobs[index] = mob
    }
}
