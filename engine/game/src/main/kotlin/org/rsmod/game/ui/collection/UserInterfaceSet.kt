package org.rsmod.game.ui.collection

import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unimi.dsi.fastutil.ints.IntSet
import org.rsmod.game.ui.UserInterface

@JvmInline
public value class UserInterfaceSet(public val backing: IntSet = IntOpenHashSet()) : Iterable<Int> {
    public fun isEmpty(): Boolean = backing.isEmpty()

    public fun isNotEmpty(): Boolean = !isEmpty()

    public fun clear() {
        backing.clear()
    }

    public operator fun plusAssign(ui: UserInterface) {
        backing.add(ui.id)
    }

    public operator fun minusAssign(ui: Int) {
        backing.remove(ui)
    }

    public operator fun contains(ui: UserInterface): Boolean {
        return backing.contains(ui.id)
    }

    override fun iterator(): Iterator<Int> = backing.iterator()

    override fun toString(): String = backing.map { UserInterface(it) }.toString()
}
