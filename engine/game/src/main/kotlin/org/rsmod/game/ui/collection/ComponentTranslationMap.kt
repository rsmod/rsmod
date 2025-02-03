package org.rsmod.game.ui.collection

import it.unimi.dsi.fastutil.ints.Int2IntLinkedOpenHashMap
import it.unimi.dsi.fastutil.ints.Int2IntMap
import org.rsmod.game.ui.Component

@JvmInline
public value class ComponentTranslationMap(
    public val backing: Int2IntMap = Int2IntLinkedOpenHashMap()
) : Iterable<Map.Entry<Int, Int>> {
    public fun isEmpty(): Boolean = backing.isEmpty()

    public fun isNotEmpty(): Boolean = !isEmpty()

    public fun clear() {
        backing.clear()
    }

    public fun getOrNull(key: Component): Component? {
        val component = this[key]
        return if (component == Component.NULL) null else component
    }

    public fun remove(key: Component): Boolean =
        backing.remove(key.packed) != backing.defaultReturnValue()

    public operator fun set(key: Component, value: Component) {
        backing[key.packed] = value.packed
    }

    public operator fun get(key: Component): Component =
        if (key in this) {
            Component(backing[key.packed])
        } else {
            Component.NULL
        }

    public operator fun contains(key: Component): Boolean = backing.containsKey(key.packed)

    // NOTE: we use the default entrySet, which is deprecated in fastutil collections.
    // Doing this to avoid using their default Entry pair implementation, which developers may feel
    // inclined to specify in their code such as: val iterator: Iterator<Int2IntMap.Entry>
    override fun iterator(): Iterator<Map.Entry<Int, Int>> = backing.iterator()

    override fun toString(): String =
        backing.int2IntEntrySet().map { Component(it.intKey) to Component(it.intValue) }.toString()
}
