package org.rsmod.api.registry.npc

import org.rsmod.game.entity.Npc

@JvmInline
public value class ZoneNpcEntryList(public val entries: ArrayDeque<Npc>) {
    public val size: Int
        get() = entries.size

    public constructor() : this(ArrayDeque())

    public fun add(entry: Npc) {
        entries.addLast(entry)
    }

    public fun remove(entry: Npc): Boolean = entries.remove(entry)

    public operator fun contains(entry: Npc): Boolean = entry in entries
}
