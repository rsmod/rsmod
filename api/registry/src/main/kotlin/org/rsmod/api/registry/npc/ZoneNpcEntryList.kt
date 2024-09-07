package org.rsmod.api.registry.npc

import java.util.LinkedList
import org.rsmod.game.entity.Npc

@JvmInline
public value class ZoneNpcEntryList(public val entries: LinkedList<Npc>) {
    public val size: Int
        get() = entries.size

    public constructor() : this(LinkedList())

    // TODO: Find out if npcs are FIFO or LIFO
    public fun add(entry: Npc) {
        entries.addFirst(entry)
    }

    public fun remove(entry: Npc): Boolean = entries.remove(entry)

    public operator fun contains(entry: Npc): Boolean = entry in entries
}
