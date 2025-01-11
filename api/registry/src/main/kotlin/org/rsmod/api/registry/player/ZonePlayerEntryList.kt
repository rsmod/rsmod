package org.rsmod.api.registry.player

import org.rsmod.game.entity.Player

@JvmInline
public value class ZonePlayerEntryList(public val entries: ArrayDeque<Player>) {
    public val size: Int
        get() = entries.size

    public constructor() : this(ArrayDeque())

    public fun add(entry: Player) {
        entries.addFirst(entry)
    }

    public fun remove(entry: Player): Boolean = entries.remove(entry)

    public operator fun contains(entry: Player): Boolean = entry in entries
}
