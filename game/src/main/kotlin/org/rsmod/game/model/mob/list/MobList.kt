package org.rsmod.game.model.mob.list

import org.rsmod.game.model.mob.Mob

public sealed class MobList<T : Mob>(
    public val capacity: Int,
    private val mobs: MutableList<T?> = MutableList(capacity) { null }
) : List<T?> by mobs {

    override val size: Int
        get() = mobs.count { it != null }

    public val indices: IntRange
        get() = INDEX_PADDING until mobs.size

    override fun isEmpty(): Boolean {
        return mobs.all { it == null }
    }

    public operator fun set(index: Int, mob: T?) {
        if (index !in indices) {
            throw IndexOutOfBoundsException("Index out of bounds (index=$index, valid=$indices).")
        }
        mobs[index] = mob
    }

    public fun indexOfFirstNullOrNull(): Int? {
        for (i in indices) {
            if (this[i] == null) return i
        }
        return null
    }

    public companion object {

        public const val INDEX_PADDING: Int = 1
    }
}
