package org.rsmod.api.registry.controller

import org.rsmod.game.entity.Controller

@JvmInline
public value class ZoneControllerEntryList(public val entries: ArrayDeque<Controller>) {
    public val size: Int
        get() = entries.size

    public constructor() : this(ArrayDeque())

    public fun add(entry: Controller) {
        entries.addFirst(entry)
    }

    public fun remove(entry: Controller): Boolean = entries.remove(entry)

    public operator fun contains(entry: Controller): Boolean = entry in entries
}
