package org.rsmod.game.ui.collection

import it.unimi.dsi.fastutil.ints.Int2IntMap
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import kotlin.collections.set
import org.rsmod.game.ui.Component
import org.rsmod.game.ui.UserInterface

@JvmInline
public value class ComponentTargetMap(public val backing: Int2IntMap = Int2IntOpenHashMap()) :
    Iterable<Map.Entry<Int, Int>> {
    public val keys: Collection<Int>
        get() = backing.keys

    public val values: Collection<Int>
        get() = backing.values

    public fun isEmpty(): Boolean = backing.isEmpty()

    public fun isNotEmpty(): Boolean = !isEmpty()

    public fun entries(): Iterable<Map.Entry<Int, Int>> = backing.int2IntEntrySet()

    public fun remove(key: Component): UserInterface? {
        val removed = backing.remove(key.packed)
        return if (removed != backing.defaultReturnValue()) {
            UserInterface(removed)
        } else {
            null
        }
    }

    public fun getComponent(occupiedBy: UserInterface): Component? {
        val entries = backing.int2IntEntrySet()
        val key = entries.firstOrNull { it.intValue == occupiedBy.id } ?: return null
        return Component(key.intKey)
    }

    public operator fun set(key: Component, value: UserInterface) {
        backing[key.packed] = value.id
    }

    public operator fun get(key: Component): UserInterface =
        if (key in this) {
            UserInterface(backing[key.packed])
        } else {
            UserInterface.NULL
        }

    public operator fun contains(key: Component): Boolean = backing.containsKey(key.packed)

    // NOTE: we use the default entrySet, which is deprecated in fastutil collections.
    // Doing this to avoid using their default Entry pair implementation, which developers may feel
    // inclined to specify in their code such as: val iterator: Iterator<Int2IntMap.Entry>
    override fun iterator(): Iterator<Map.Entry<Int, Int>> = backing.iterator()

    override fun toString(): String =
        backing
            .int2IntEntrySet()
            .map { Component(it.intKey) to UserInterface(it.intValue) }
            .toString()
}
