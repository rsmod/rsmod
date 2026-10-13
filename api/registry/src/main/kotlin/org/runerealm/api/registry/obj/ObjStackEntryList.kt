package org.runerealm.api.registry.obj

import org.runerealm.game.obj.Obj
import org.runerealm.map.CoordGrid

private typealias ObjStackEntry = Obj

@JvmInline
public value class ObjStackEntryList(public val entries: ArrayDeque<ObjStackEntry>) {
    public val size: Int
        get() = entries.size

    public constructor() : this(ArrayDeque())

    public fun add(entry: ObjStackEntry) {
        entries.addLast(entry)
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
