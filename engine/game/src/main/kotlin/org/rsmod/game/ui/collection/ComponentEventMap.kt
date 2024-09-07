package org.rsmod.game.ui.collection

import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.rsmod.game.ui.Component
import org.rsmod.game.ui.ComponentEvents

@JvmInline
public value class ComponentEventMap(
    public val backing: Int2ObjectMap<ComponentEvents> = Int2ObjectOpenHashMap()
) : Iterable<Map.Entry<Int, ComponentEvents>> {
    public fun isEmpty(): Boolean = backing.isEmpty()

    public fun isNotEmpty(): Boolean = !isEmpty()

    public operator fun set(key: Component, event: ComponentEvents) {
        backing[key.packed] = event
    }

    public operator fun get(key: Component): ComponentEvents? =
        backing.getOrDefault(key.packed, null)

    public operator fun contains(key: Component): Boolean = backing.containsKey(key.packed)

    // NOTE: we use the default entrySet, which is deprecated in fastutil collections.
    // Doing this to avoid using their default Entry pair implementation, which developers may feel
    // inclined to specify in their code such as: val iterator: Iterator<Int2ObjectMap.Entry<V>>
    override fun iterator(): Iterator<Map.Entry<Int, ComponentEvents>> = backing.iterator()
}
