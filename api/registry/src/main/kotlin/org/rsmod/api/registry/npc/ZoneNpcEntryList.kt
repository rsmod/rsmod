package org.rsmod.api.registry.npc

import org.rsmod.game.entity.Npc

@JvmInline
public value class ZoneNpcEntryList(public val entries: ArrayDeque<Npc>) {
    public val size: Int
        get() = entries.size

    public constructor() : this(ArrayDeque())

    // TODO: Find out if npcs are FIFO or LIFO
    public fun add(entry: Npc) {
        entries.addFirst(entry)
    }

    public fun remove(entry: Npc): Boolean = entries.remove(entry)

    public operator fun contains(entry: Npc): Boolean = entry in entries
}
