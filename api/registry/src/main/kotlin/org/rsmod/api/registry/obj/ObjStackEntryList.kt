package org.rsmod.api.registry.obj

import java.util.LinkedList
import org.rsmod.game.obj.Obj
import org.rsmod.map.CoordGrid

private typealias ObjStackEntry = Obj

@JvmInline
public value class ObjStackEntryList(public val entries: LinkedList<ObjStackEntry>) {
    public val size: Int
        get() = entries.size

    public constructor() : this(LinkedList())

    public fun add(entry: ObjStackEntry) {
        entries.addFirst(entry)
    }

    public fun remove(entry: ObjStackEntry): Boolean = entries.remove(entry)

    public fun findAll(coords: CoordGrid): Sequence<ObjStackEntry> = sequence {
        for (entry in entries) {
            if (entry.coords == coords) {
                yield(entry)
            }
        }
    }

    public operator fun contains(obj: ObjStackEntry): Boolean = entries.contains(obj)
}
