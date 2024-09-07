package org.rsmod.game.entity

import org.rsmod.map.CoordGrid
import org.rsmod.map.util.Bounds

public sealed class PathingEntityAvatar(
    public val size: Int,
    public var coords: CoordGrid = CoordGrid.ZERO,
    public var previousCoords: CoordGrid = CoordGrid.ZERO,
) {
    public var x: Int
        get() = coords.x
        set(value) {
            coords = coords.copy(x = value)
        }

    public var z: Int
        get() = coords.z
        set(value) {
            coords = coords.copy(z = value)
        }

    public var level: Int
        get() = coords.level
        set(value) {
            coords = coords.copy(level = value)
        }

    public fun bounds(): Bounds = Bounds(coords, size, size)

    /**
     * @throws IllegalArgumentException if [target]'s [Bounds.level] is not equal to this [level].
     */
    public fun distanceTo(target: Bounds): Int = bounds().distanceTo(target)

    /**
     * @return true if the [target] is within [distance] tiles from [coords] _and_ the [target]'s
     *   [Bounds.level] is equal to this [level].
     *
     * This takes into account width and length dimensions for both [target] and this avatar.
     */
    public fun isWithinDistance(target: Bounds, distance: Int): Boolean =
        level == target.level && bounds().isWithinDistance(target, distance)
}
